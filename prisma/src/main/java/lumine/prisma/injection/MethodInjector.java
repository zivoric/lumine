package lumine.prisma.injection;

import lumine.prisma.injection.annotations.*;
import lumine.prisma.injection.generic.GenericMethod;
import lumine.prisma.injection.util.InjectProperties;
import lumine.prisma.injection.util.MethodGrabber;
import lumine.prisma.injection.util.MethodInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class MethodInjector<T> {

    private static final Set<Class<? extends Annotation>> injectAnnotations = new HashSet<>() {{
        add(InvokeInjection.class);
        add(ReplaceInjection.class);
        add(ReturnInjection.class);
    }};

    private final MethodInfo method;
    private final InjectorMethodList injectorMethods = new InjectorMethodList();

    protected MethodInjector(MethodGrabber.LambdaGrabber func) {
        this(Objects.requireNonNull(MethodGrabber.fromLambda(func)));
    }

    protected MethodInjector(GenericMethod<?> generic) {
        this(MethodGrabber.fromGeneric(Objects.requireNonNull(generic)));
    }

    protected MethodInjector(MethodInfo method) {
        this.method = method;
        for (Method m : this.getClass().getDeclaredMethods()) {
            boolean cache = m.getAnnotation(CacheValue.class) != null;
            boolean pass = m.getAnnotation(PassInstance.class) != null;
            for (Class<? extends Annotation> annotation : injectAnnotations) {
                if (m.getAnnotation(annotation) != null) {
                    injectorMethods.put(m, m.getAnnotation(annotation), cache, pass);
                    break;
                }
            }
        }
    }

    static InjectorMethodList getStaticInjectorMethods(Class<? extends MethodInjector<?>> cl) {
        final InjectorMethodList injectorMethods = new InjectorMethodList();
        for (Method m : cl.getDeclaredMethods()) {
            boolean cache = m.getAnnotation(CacheValue.class) != null;
            boolean pass = m.getAnnotation(PassInstance.class) != null;
            for (Annotation annotation : m.getAnnotations()) {
                for (Class<? extends Annotation> inject : injectAnnotations) {
                    if (inject.getName().equals(annotation.annotationType().getName())) {
                        injectorMethods.put(m, null, cache, pass);
                    }
                }
            }
        }
        return injectorMethods;
    }

    public final InjectorMethodList getInjectorMethods() {
        return injectorMethods;
    }

    public final MethodInfo info() {
        return method;
    }

    public final String owner() {
        return method.owner();
    }

    public final String name() {
        return method.name();
    }

    public final String desc() {
        return method.desc();
    }

    public abstract InjectProperties.Context getContext();

    public static final class InjectorMethodList extends ArrayList<InjectorInfo> {
        public void put(Method m, Annotation a, boolean cache, boolean pass) {
            add(new InjectorInfo(m, a, cache, pass));
        }
        public int remove(Method m, Annotation a) {
            int count = 0;
            for (InjectorInfo pair : this) {
                if (pair.method.equals(m) && pair.annotation.equals(a)) {
                    remove(pair);
                    count++;
                }
            }
            return count;
        }
        public int remove(Method m) {
            int count = 0;
            for (InjectorInfo pair : this) {
                if (pair.method.equals(m)) {
                    remove(pair);
                    count++;
                }
            }
            return count;
        }
        public InjectorInfo get(MethodInfo mPair) {
            for (InjectorInfo pair : this) {
                MethodInfo fromInjector = MethodInfo.fromMethod(pair.method);
                if (fromInjector.equals(mPair))
                    return pair;
            }
            return null;
        }
    }

    public static final class InjectorInfo {
        private final Method method;
        private final Annotation annotation;
        private final boolean cacheValue;
        private final boolean passInstance;
        private InjectorInfo(Method m, Annotation a, boolean cache, boolean pass) {
            method = m;
            annotation = a;
            cacheValue = cache;
            passInstance = pass;
        }
        public Method method() {
            return method;
        }
        public Annotation annotation() {
            return annotation;
        }
        public boolean cache() {
            return cacheValue;
        }
        public boolean passInstance() {
            return passInstance;
        }
    }
}
