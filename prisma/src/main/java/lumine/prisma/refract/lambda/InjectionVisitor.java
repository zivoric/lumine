package lumine.prisma.refract.lambda;

import lumine.prisma.refract.lambda.annotations.types.InvokeInjection;
import lumine.prisma.refract.lambda.annotations.types.ReplaceInjection;
import lumine.prisma.refract.lambda.annotations.types.ReturnInjection;
import lumine.prisma.refract.definition.method.InjectPoint;
import lumine.prisma.refract.util.RefractionUtils;
import lumine.prisma.refract.MethodInfo;
import lumine.prisma.refract.visitor.ReturnInvokeAdapter;
import lumine.prisma.refract.visitor.StartInvokeAdapter;
import lumine.prisma.launch.Prisma;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class InjectionVisitor extends ClassVisitor {
    private final ClassInjector<?> clInjector;
    public InjectionVisitor(ClassVisitor classVisitor, ClassInjector<?> injector) {
        super(Opcodes.ASM9, classVisitor);
        clInjector = injector;
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        MethodInfo pair = new MethodInfo(name, desc);
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        for (MethodInjector<?> injector : clInjector.methodInjectors()) {
            if (name.equals(injector.name()) && desc.equals(injector.desc())) {
                for (MethodInjector.InjectorInfo injectorMethod : injector.getInjectorMethods()) {
                    if (injectorMethod.annotation() instanceof InvokeInjection annotation) {
                        Prisma.getLogger().info("Injecting invoke to " + injectorMethod.method().getName() + " in " + name + desc + " at " + annotation.value());
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache(), injectorMethod.passInstance());
                        invokeInsns.add(new InsnNode(Opcodes.POP));
                        if (annotation.value() == InjectPoint.START) {
                            mv = new StartInvokeAdapter(mv, access, name, desc, invokeInsns);
                        } else if (annotation.value() == InjectPoint.RETURN) {
                            mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                        }
                    } else if (injectorMethod.annotation() instanceof ReturnInjection) {
                        Prisma.getLogger().info("Injecting return to " + injectorMethod.method().getName() + " in " + name + desc);
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache(), injectorMethod.passInstance());
                        invokeInsns.insert(new InsnNode(Opcodes.POP));
                        invokeInsns.add(getCastInsns(pair.strType()));
                        mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                    } else if (injectorMethod.annotation() instanceof ReplaceInjection) {
                        Prisma.getLogger().info("Injecting replace to " + injectorMethod.method().getName() + " in " + name + desc);
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache(), injectorMethod.passInstance());
                        invokeInsns.add(getCastInsns(pair.strType()));
                        invokeInsns.add(new InsnNode(Type.getType(pair.strType()).getOpcode(Opcodes.IRETURN)));
                        mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                    }
                }
            }
        }
        return mv;
    }
    private InsnList invokeInsns(MethodInjector injector, MethodInjector.InjectorInfo pair, boolean isStatic, boolean cacheValue, boolean passInstance) {
        //Prisma.getLogger().info("Method " + injector.name() + " pass instance? " + passInstance);
        MethodInfo mPair = MethodInfo.fromMethod(pair.method());
        String[] params = mPair.stringArgs();
        //Prisma.getLogger().info("Params length " + params.length);
        int arrLength = params.length; //+ (isStatic || !passInstance ? 0 : 1);
        int offset = !isStatic && passInstance ? 1 : 0;
        InsnList list = new InsnList() {{
            add(new LdcInsnNode(injector.getClass().getName()));
            add(new LdcInsnNode(mPair.toString()));
            add(new InsnNode(cacheValue ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
            add(new LdcInsnNode(arrLength));
            add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
            if (!isStatic && passInstance) {
                //Prisma.getLogger().info("load this");
                add(new InsnNode(Opcodes.DUP));
                add(new LdcInsnNode(0));
                add(new VarInsnNode(Opcodes.ALOAD, 0)); // aload this
                add(new InsnNode(Opcodes.AASTORE));
            }
            for (int argNum = offset; argNum < params.length; argNum++) {
                //Prisma.getLogger().info("Arg num " + argNum+offset + ", param type " + params[argNum]);
                add(new InsnNode(Opcodes.DUP));
                add(new LdcInsnNode(argNum));
                add(getLoadInsns(params[argNum], argNum-offset+(isStatic ? 0 : 1)));
                add(new InsnNode(Opcodes.AASTORE));
            }
            add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumine/prisma/refract/ClassInjector", "invoke", "(Ljava/lang/String;Ljava/lang/String;Z[Ljava/lang/Object;)Ljava/lang/Object;"));
        }};
        return list;
    }

    private InsnList getLoadInsns(String param, int index) {
        Class<?> boxed = RefractionUtils.BOXED.get(param.charAt(0));
        return new InsnList() {{
            if (boxed != null) {
                int opcode = Type.getType(param).getOpcode(Opcodes.ILOAD);
                add(new VarInsnNode(opcode, index));
                String unboxedName = boxed.getName().replace('.', '/');
                add(new MethodInsnNode(Opcodes.INVOKESTATIC, unboxedName, "valueOf", "(" + param.charAt(0) + ")L" + unboxedName + ";"));
            } else {
                //Prisma.getLogger().info("ALOAD " + index + ", " + param);
                add(new VarInsnNode(Opcodes.ALOAD, index));
            }
        }};
    }
    private InsnList getCastInsns(String type) {
        InsnList list = new InsnList();
        Class<?> boxed = RefractionUtils.BOXED.get(type.charAt(0));
        if (boxed != null) {
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, MethodInfo.typeToString(boxed)));
            String methodName = RefractionUtils.UNBOXED.get(type.charAt(0)).getSimpleName() + "Value";
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, boxed.getName().replace('.', '/'), methodName, "()" + type));
        } else {
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, type.substring(1,type.length()-1)));
        }
        return list;
    }
}
