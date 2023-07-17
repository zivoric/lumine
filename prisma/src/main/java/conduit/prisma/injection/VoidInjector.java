package conduit.prisma.injection;

import conduit.prisma.injection.util.InjectProperties;
import conduit.prisma.injection.util.MethodGrabber;
import conduit.prisma.injection.util.MethodInfo;

public class VoidInjector<T> extends MethodInjector<T> {
    private final InjectProperties.Context context;
    private VoidInjector(MethodGrabber.LambdaGrabber func, InjectProperties.Context context) {
        super(func);
        this.context = context;
    }
    public VoidInjector(MethodInfo method, InjectProperties.Context context) {
        super(method);
        this.context = context;
    }
    @Override
    public InjectProperties.Context getContext() {
        return context;
    }

    public <A> VoidInjector(Class<T> cl, MethodGrabber.Void1<A> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B> VoidInjector(Class<T> cl, MethodGrabber.Void2<A,B> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C> VoidInjector(Class<T> cl, MethodGrabber.Void3<A,B,C> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D> VoidInjector(Class<T> cl, MethodGrabber.Void4<A,B,C,D> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D,E> VoidInjector(Class<T> cl, MethodGrabber.Void5<A,B,C,D,E> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D,E,F> VoidInjector(Class<T> cl, MethodGrabber.Void6<A,B,C,D,E,F> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }

    public VoidInjector(MethodGrabber.Void0 func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A> VoidInjector(MethodGrabber.Void1<A> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B> VoidInjector(MethodGrabber.Void2<A,B> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C> VoidInjector(MethodGrabber.Void3<A,B,C> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D> VoidInjector(MethodGrabber.Void4<A,B,C,D> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E> VoidInjector(MethodGrabber.Void5<A,B,C,D,E> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E,F> VoidInjector(MethodGrabber.Void6<A,B,C,D,E,F> func) {
        this(func, InjectProperties.Context.STATIC);
    }
}
