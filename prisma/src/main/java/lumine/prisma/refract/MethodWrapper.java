package lumine.prisma.refract;

import lumine.prisma.refract.definition.method.RefractMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;

public class MethodWrapper {
    private static final HashSet<Class<? extends Annotation>> INJECTOR_ANNOTATIONS = new HashSet<>() {{
       add(RefractMethod.class);
    }};

    private final MethodInfo methodInfo;
    private final Method method;
    private Annotation annotation = null;
    private Class<? extends Annotation> annotationClass = null;

    protected MethodWrapper(Method method) {
        this.methodInfo = MethodInfo.fromMethod(method);
        this.method = method;
        for (Class<? extends Annotation> annotation : INJECTOR_ANNOTATIONS) {
            if (method.getAnnotation(annotation) != null) {
                this.annotation = method.getAnnotation(annotation);
                this.annotationClass = this.annotation.annotationType();
                break;
            }
        }

        if (annotationClass != null) {
            if (annotation instanceof RefractMethod rMethod) {
                rMethod.
            }
        }
    }

    public final MethodInfo info() {
        return methodInfo;
    }

    public final String owner() {
        return methodInfo.owner();
    }

    public final String name() {
        return methodInfo.name();
    }

    public final String desc() {
        return methodInfo.desc();
    }

    public final String identifier() {
        return methodInfo.owner() + "." + methodInfo.toString();
    }

    public final Method method() {
        return method;
    }

    public final Annotation annotation() {
        return annotation;
    }

    public final Class<? extends Annotation> annotationClass() {
        return annotationClass;
    }

    public final Class<?> refractorClass() {
        return method.getDeclaringClass();
    }
}
