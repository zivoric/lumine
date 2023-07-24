package lumine.prisma.refract.lambda;

import lumine.prisma.refract.MethodInfo;
import lumine.prisma.refract.definition.method.InjectContext;

public class VoidInjector<T> extends MethodInjector<T> {
    private final InjectContext context;
    private VoidInjector(MethodGrabber.LambdaGrabber func, InjectContext context) {
        super(func);
        this.context = context;
    }
    public VoidInjector(MethodInfo method, InjectContext context) {
        super(method);
        this.context = context;
    }
    @Override
    public InjectContext getContext() {
        return context;
    }

    public <A> VoidInjector(Class<T> cl, MethodGrabber.Void1<A> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B> VoidInjector(Class<T> cl, MethodGrabber.Void2<A,B> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C> VoidInjector(Class<T> cl, MethodGrabber.Void3<A,B,C> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D> VoidInjector(Class<T> cl, MethodGrabber.Void4<A,B,C,D> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D,E> VoidInjector(Class<T> cl, MethodGrabber.Void5<A,B,C,D,E> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D,E,F> VoidInjector(Class<T> cl, MethodGrabber.Void6<A,B,C,D,E,F> func) {
        this(func, InjectContext.INSTANCE);
    }

    public VoidInjector(MethodGrabber.Void0 func) {
        this(func, InjectContext.STATIC);
    }
    public <A> VoidInjector(MethodGrabber.Void1<A> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B> VoidInjector(MethodGrabber.Void2<A,B> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C> VoidInjector(MethodGrabber.Void3<A,B,C> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D> VoidInjector(MethodGrabber.Void4<A,B,C,D> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D,E> VoidInjector(MethodGrabber.Void5<A,B,C,D,E> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D,E,F> VoidInjector(MethodGrabber.Void6<A,B,C,D,E,F> func) {
        this(func, InjectContext.STATIC);
    }
}
