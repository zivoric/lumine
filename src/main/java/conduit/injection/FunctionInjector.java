package conduit.injection;

import conduit.injection.util.InjectProperties;
import conduit.injection.util.MethodGrabber.*;
import conduit.injection.util.MethodInfo;

public class FunctionInjector<T> extends MethodInjector<T> {
    private final InjectProperties.Context context;
    public FunctionInjector(LambdaGrabber func, InjectProperties.Context context) {
        super(func);
        this.context = context;
    }
    public FunctionInjector(MethodInfo method, InjectProperties.Context context) {
        super(method);
        this.context = context;
    }

    @Override
    public InjectProperties.Context getContext() {
        return context;
    }
    public <R> FunctionInjector(Class<T> cl, Args1<T,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <B,R> FunctionInjector(Class<T> cl, Args2<T,B,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <B,C,R> FunctionInjector(Class<T> cl, Args3<T,B,C,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <B,C,D,R> FunctionInjector(Class<T> cl, Args4<T,B,C,D,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <B,C,D,E,R> FunctionInjector(Class<T> cl, Args5<T,B,C,D,E,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <B,C,D,E,F,R> FunctionInjector(Class<T> cl, Args6<T,B,C,D,E,F,R> func) {
        this(func, InjectProperties.Context.INSTANCE);
    }
    public <R> FunctionInjector(Args0<R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,R> FunctionInjector(Args1<A,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,R> FunctionInjector(Args2<A,B,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,R> FunctionInjector(Args3<A,B,C,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,R> FunctionInjector(Args4<A,B,C,D,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E,R> FunctionInjector(Args5<A,B,C,D,E,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
    public <A,B,C,D,E,F,R> FunctionInjector(Args6<A,B,C,D,E,F,R> func) {
        this(func, InjectProperties.Context.STATIC);
    }
}
