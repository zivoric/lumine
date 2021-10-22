package conduit.injection;

import conduit.injection.util.InjectProperties;
import conduit.injection.util.MethodGrabber.*;
import conduit.injection.util.MethodInfo;

public class VoidInjector<T> extends MethodInjector<T> {
    private final InjectProperties.Context context;
    private VoidInjector(LambdaGrabber func, InjectProperties.Context context) {
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

    public <A> VoidInjector(Class<T> cl, Void1<A> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B> VoidInjector(Class<T> cl, Void2<A,B> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C> VoidInjector(Class<T> cl, Void3<A,B,C> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D> VoidInjector(Class<T> cl, Void4<A,B,C,D> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D,E> VoidInjector(Class<T> cl, Void5<A,B,C,D,E> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <A,B,C,D,E,F> VoidInjector(Class<T> cl, Void6<A,B,C,D,E,F> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }

    public VoidInjector(Void0 func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A> VoidInjector(Void1<A> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B> VoidInjector(Void2<A,B> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C> VoidInjector(Void3<A,B,C> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D> VoidInjector(Void4<A,B,C,D> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E> VoidInjector(Void5<A,B,C,D,E> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E,F> VoidInjector(Void6<A,B,C,D,E,F> func) {
        this(func, InjectProperties.Context.STATIC);
    }
}
