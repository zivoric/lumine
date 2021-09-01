package conduit.injection;

import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.annotations.ReturnInjection;
import conduit.injection.util.InjectProperties;
import conduit.injection.util.InjectionUtils;
import conduit.injection.util.MethodInfo;
import conduit.injection.visitor.ReturnInvokeAdapter;
import conduit.injection.visitor.StartInvokeAdapter;
import conduit.main.Conduit;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class InjectionVisitor extends ClassVisitor {
    private final ClassInjector clInjector;
    public InjectionVisitor(ClassVisitor classVisitor, ClassInjector injector) {
        super(Opcodes.ASM9, classVisitor);
        clInjector = injector;
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        MethodInfo pair = new MethodInfo(name, desc);
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        for (MethodInjector injector : clInjector.methodInjectors()) {
            if (name.equals(injector.name()) && desc.equals(injector.desc())) {
                for (MethodInjector.InjectorInfo injectorMethod : injector.getInjectorMethods()) {
                    if (injectorMethod.annotation() instanceof InvokeInjection annotation) {
                        Conduit.log("Injecting invoke to " + injectorMethod.method().getName() + " in " + name + desc + " at " + annotation.value());
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache());
                        invokeInsns.add(new InsnNode(Opcodes.POP));
                        if (annotation.value() == InjectProperties.Point.START) {
                            mv = new StartInvokeAdapter(mv, access, name, desc, invokeInsns);
                        } else if (annotation.value() == InjectProperties.Point.RETURN) {
                            mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                        }
                    } else if (injectorMethod.annotation() instanceof ReturnInjection) {
                        Conduit.log("Injecting return to " + injectorMethod.method().getName() + " in " + name + desc);
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache());
                        invokeInsns.insert(new InsnNode(Opcodes.POP));
                        invokeInsns.add(getCastInsns(pair.strType()));
                        mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                    } else if (injectorMethod.annotation() instanceof ReplaceInjection) {
                        Conduit.log("Injecting replace to " + injectorMethod.method().getName() + " in " + name + desc);
                        InsnList invokeInsns = invokeInsns(injector, injectorMethod, isStatic, injectorMethod.cache());
                        invokeInsns.add(getCastInsns(pair.strType()));
                        invokeInsns.add(new InsnNode(Type.getType(pair.strType()).getOpcode(Opcodes.IRETURN)));
                        mv = new ReturnInvokeAdapter(mv, access, name, desc, invokeInsns);
                    }
                }
            }
        }
        return mv;
    }
    private InsnList invokeInsns(MethodInjector injector, MethodInjector.InjectorInfo pair, boolean isStatic, boolean cacheValue) {
        MethodInfo mPair = MethodInfo.fromMethod(pair.method());
        String[] params = mPair.stringArgs();
        InsnList list = new InsnList() {{
            add(new LdcInsnNode(injector.getClass().getName()));
            add(new LdcInsnNode(mPair.toString()));
            add(new InsnNode(cacheValue ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
            add(new LdcInsnNode(params.length));
            add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
            if (params.length > 0) {
                add(new InsnNode(Opcodes.DUP));
            }
            if (!isStatic) {
                add(new LdcInsnNode(0));
                add(new VarInsnNode(Opcodes.ALOAD, 0)); // aload this
                add(new InsnNode(Opcodes.AASTORE));
                if (params.length > 1) {
                    add(new InsnNode(Opcodes.DUP));
                }
            }
            for (int argNum = isStatic ? 0 : 1; argNum < params.length; argNum++) {
                add(new LdcInsnNode(argNum));
                add(getLoadInsns(params[argNum], argNum));
                add(new InsnNode(Opcodes.AASTORE));
                if (argNum < params.length - 1) {
                    add(new InsnNode(Opcodes.DUP));
                }
            }
            add(new MethodInsnNode(Opcodes.INVOKESTATIC, "conduit/injection/ClassInjector", "invoke", "(Ljava/lang/String;Ljava/lang/String;Z[Ljava/lang/Object;)Ljava/lang/Object;"));
        }};
        return list;
    }

    private InsnList getLoadInsns(String param, int index) {
        Class<?> boxed = InjectionUtils.BOXED.get(param.charAt(0));
        return new InsnList() {{
            if (boxed != null) {
                int opcode = Type.getType(param).getOpcode(Opcodes.ILOAD);
                add(new VarInsnNode(opcode, index));
                String unboxedName = boxed.getName().replace('.', '/');
                add(new MethodInsnNode(Opcodes.INVOKESTATIC, unboxedName, "valueOf", "(" + param.charAt(0) + ")L" + unboxedName + ";"));
            } else {
                add(new VarInsnNode(Opcodes.ALOAD, index));
            }
        }};
    }
    private InsnList getCastInsns(String type) {
        InsnList list = new InsnList();
        Class<?> boxed = InjectionUtils.BOXED.get(type.charAt(0));
        if (boxed != null) {
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, MethodInfo.typeToString(boxed)));
            String methodName = InjectionUtils.UNBOXED.get(type.charAt(0)).getSimpleName() + "Value";
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, boxed.getName().replace('.', '/'), methodName, "()" + type));
        } else {
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, type.substring(1,type.length()-1)));
        }
        return list;
    }
}
