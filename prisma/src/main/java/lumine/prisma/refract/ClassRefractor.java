package lumine.prisma.refract;

import lumine.prisma.launch.Prisma;
import lumine.prisma.refract.definition.Refract;
import lumine.prisma.refract.definition.method.RefractMethod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ClassRefractor {
    private static final InjectionCache CACHE = new InjectionCache();
    private final String targetClass;

    private static final HashMap<String, MethodWrapper> INJECTOR_METHODS = new HashMap<>();

    private final Collection<MethodWrapper> injectorMethods = new LinkedHashSet<>();

    public ClassRefractor(Class<?> refractorClass) {
        if (refractorClass.getAnnotation(Refract.class) != null) {
            Refract refract = refractorClass.getAnnotation(Refract.class);
            targetClass = Type.getInternalName(refract.value());
            for (Method m : refractorClass.getDeclaredMethods()) {
                MethodWrapper wrapper = new MethodWrapper(m);
                if (wrapper.annotationClass() != null) {
                    INJECTOR_METHODS.put(wrapper.identifier(), wrapper);
                    injectorMethods.add(wrapper);
                }
            }
        } else {
            throw new IllegalArgumentException("Class '" + refractorClass.getName() + "' is not a refractor class");
        }
    }

    public Collection<MethodWrapper> getInjectorMethods() {
        return injectorMethods;
    }

    public static byte[] transformAll(byte[] currentClass, Iterable<ClassRefractor> refractors) {
        ClassReader cr = new ClassReader(currentClass);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        boolean transformed = false;
        for (ClassRefractor refractor : refractors) {
            if (cr.getClassName().equals(refractor.targetClass)) {
                RefractionVisitor visitor = new RefractionVisitor(cw, refractor);
                cr.accept(visitor, ClassReader.EXPAND_FRAMES);
                transformed = true;
            }
        }
        if (transformed) {
            return cw.toByteArray();
        } else {
            return currentClass;
        }
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(String injectClass, String pairStr, boolean cacheValue, Object... args) {
        /*Lumine.getLogger().info("Invoke args length: " + args.length);
        for (Object arg : args) {
            Lumine.getLogger().info("Argument type: " + arg.getClass().getName());
        }*/
        MethodInfo pair = MethodInfo.fromString(pairStr);
        Object cached = CACHE.get(pair.toString());
        if (cacheValue && cached != null) {
            return cached;
        }
        try {
            Class<? extends MethodInjector<?>> classObj = (Class<? extends MethodInjector<?>>) Class.forName(injectClass);
            MethodInjector.InjectorInfo iPair = MethodInjector.getStaticInjectorMethods(classObj).get(pair);
            iPair.method().setAccessible(true);
            Object result = iPair.method().invoke(null, args);
            if (cacheValue) {
                CACHE.put(pair.toString(), result);
            }
            return result;
        } catch (InvocationTargetException e) {
            Prisma.getLogger().error("Method error while invoking " + pair + ":", e.getTargetException());
            return null;
        } catch (Exception e) {
            Prisma.getLogger().error("Unable to invoke injected method for " + pair + ":", e);
            return null;
        }
    }
}
