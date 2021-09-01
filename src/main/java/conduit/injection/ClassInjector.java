package conduit.injection;

import conduit.injection.util.MethodInfo;
import conduit.main.Conduit;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class ClassInjector<T> {
    private static final InjectionCache CACHE = new InjectionCache();
    private static final ArrayList<ClassInjector<?>> injectors = new ArrayList<>();
    private final MethodInjector[] injectMethods;
    private final String targetClass;

    @SafeVarargs
    public ClassInjector(MethodInjector<T>... methodInjectors) {
        if (methodInjectors.length > 0) {
            targetClass = methodInjectors[0].owner();
            injectMethods = methodInjectors;
        } else {
            throw new IllegalArgumentException("At least one method injector must be specified");
        }
        injectors.add(this);
    }

    public MethodInjector[] methodInjectors() {
        return injectMethods;
    }

    public static List<ClassInjector<?>> injectors() {
        return injectors;
    }

    public static byte[] transformAll(byte[] currentClass, List<ClassInjector<?>> injectors) {
        ClassReader cr = new ClassReader(currentClass);
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        boolean transformed = false;
        for (ClassInjector<?> injector : injectors) {
            if (cr.getClassName().equals(injector.targetClass)) {
                InjectionVisitor visitor = new InjectionVisitor(cw, injector);
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
            Conduit.LOGGER.error("Method error while invoking " + pair + ":", e.getTargetException());
            return null;
        } catch (Exception e) {
            Conduit.LOGGER.error("Unable to invoke injected method for " + pair + ":", e);
            return null;
        }
    }
}
