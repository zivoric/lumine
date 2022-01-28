package conduit.injection.util;

import conduit.injection.generic.GenericMethod;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

public class MethodGrabber {

    public static <A> MethodInfo voidName(Class<A> cl, Void1<A> func) {
        return fromLambda(func);
    }

    public static <A,B> MethodInfo voidName(Class<A> cl, Void2<A,B> func) {
        return fromLambda(func);
    }

    public static <A,B,C> MethodInfo voidName(Class<A> cl, Void3<A,B,C> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D> MethodInfo voidName(Class<A> cl, Void4<A,B,C,D> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E> MethodInfo voidName(Class<A> cl, Void5<A,B,C,D,E> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,F> MethodInfo voidName(Class<A> cl, Void6<A,B,C,D,E,F> func) {
        return fromLambda(func);
    }

    public static <A,R> MethodInfo methodName(Class<A> cl, Args1<A,R> func) {
        return fromLambda(func);
    }

    public static <A,B,R> MethodInfo methodName(Class<A> cl, Args2<A,B,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,R> MethodInfo methodName(Class<A> cl, Args3<A,B,C,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,R> MethodInfo methodName(Class<A> cl, Args4<A,B,C,D,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,R> MethodInfo methodName(Class<A> cl, Args5<A,B,C,D,E,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,F,R> MethodInfo methodName(Class<A> cl, Args6<A,B,C,D,E,F,R> func) {
        return fromLambda(func);
    }

    public static MethodInfo staticVoidName(Void0 func) {
        return fromLambda(func);
    }

    public static <A> MethodInfo staticVoidName(Void1<A> func) {
        return fromLambda(func);
    }

    public static <A,B> MethodInfo staticVoidName(Void2<A,B> func) {
        return fromLambda(func);
    }

    public static <A,B,C> MethodInfo staticVoidName(Void3<A,B,C> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D> MethodInfo staticVoidName(Void4<A,B,C,D> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E> MethodInfo staticVoidName(Void5<A,B,C,D,E> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,F> MethodInfo staticVoidName(Void6<A,B,C,D,E,F> func) {
        return fromLambda(func);
    }

    public static <R> MethodInfo staticMethodName(Args0<R> func) {
        return fromLambda(func);
    }

    public static <A,R> MethodInfo staticMethodName(Args1<A,R> func) {
        return fromLambda(func);
    }

    public static <A,B,R> MethodInfo staticMethodName(Args2<A,B,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,R> MethodInfo staticMethodName(Args3<A,B,C,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,R> MethodInfo staticMethodName(Args4<A,B,C,D,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,R> MethodInfo staticMethodName(Args5<A,B,C,D,E,R> func) {
        return fromLambda(func);
    }

    public static <A,B,C,D,E,F,R> MethodInfo staticMethodName(Args6<A,B,C,D,E,F,R> func) {
        return fromLambda(func);
    }

    public static <T> MethodInfo fromGeneric(GenericMethod<T> generic) {
        Class<T> cl = generic.targetClass();
        List<Method> methods = Arrays.asList(cl.getMethods());
        if (generic.name() != null) {
            methods = methods.stream().filter(mm -> mm.getName().equalsIgnoreCase(generic.name())).toList();
        }
        Method m;
        try {
            m = methods.get(generic.index());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Error finding generic method: index " + generic.index() + " is out of bounds for class " + cl);
        }
        return MethodInfo.fromMethod(m);
    }

    public static MethodInfo fromLambda(LambdaGrabber func) {
        Class<?> cl = Objects.requireNonNull(func).getClass();
        try {
            Method m = cl.getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda l = (SerializedLambda) m.invoke(func);
            return new MethodInfo(l.getImplClass(), l.getImplMethodName(), l.getImplMethodSignature());
        } catch (Exception e) {
            return null;
        }
    }

    public interface LambdaGrabber extends Serializable {}
    public interface Void0 extends Runnable, LambdaGrabber {}
    public interface Void1<A> extends Consumer<A>, LambdaGrabber {}
    public interface Void2<A, B> extends BiConsumer<A, B>, LambdaGrabber {}
    @FunctionalInterface
    public interface Void3<A, B, C> extends LambdaGrabber {void accept(A a, B b, C c);}
    @FunctionalInterface
    public interface Void4<A, B, C, D> extends LambdaGrabber {void accept(A a, B b, C c, D d);}
    @FunctionalInterface
    public interface Void5<A, B, C, D, E> extends LambdaGrabber {void accept(A a, B b, C c, D d, E e);}
    @FunctionalInterface
    public interface Void6<A, B, C, D, E, F> extends LambdaGrabber {void accept(A a, B b, C c, D d, E e, F f);}
    public interface Args0<R> extends Supplier<R>, LambdaGrabber {}
    public interface Args1<A, R> extends Function<A, R>, LambdaGrabber {}
    public interface Args2<A, B, R> extends BiFunction<A,B,R>, LambdaGrabber {}
    @FunctionalInterface
    public interface Args3<A, B, C, R> extends LambdaGrabber {R accept(A a, B b, C c);}
    @FunctionalInterface
    public interface Args4<A, B, C, D, R> extends LambdaGrabber {R accept(A a, B b, C c, D d);}
    @FunctionalInterface
    public interface Args5<A, B, C, D, E, R> extends LambdaGrabber {R accept(A a, B b, C c, D d, E e);}
    @FunctionalInterface
    public interface Args6<A, B, C, D, E, F, R> extends LambdaGrabber {R accept(A a, B b, C c, D d, E e, F f);}

}

