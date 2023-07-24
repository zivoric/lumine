package lumine.prisma.refract.lambda;

import lumine.prisma.refract.MethodInfo;
import lumine.prisma.refract.definition.method.InjectContext;

public class FunctionInjector<T> extends MethodInjector<T> {
    private final InjectContext context;
    public FunctionInjector(MethodGrabber.LambdaGrabber func, InjectContext context) {
        super(func);
        this.context = context;
    }
    public FunctionInjector(MethodInfo method, InjectContext context) {
        super(method);
        this.context = context;
    }

    @Override
    public InjectContext getContext() {
        return context;
    }
    public <A,R> FunctionInjector(Class<T> cl, MethodGrabber.Args1<A,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,R> FunctionInjector(Class<T> cl, MethodGrabber.Args2<A,B,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,R> FunctionInjector(Class<T> cl, MethodGrabber.Args3<A,B,C,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D,R> FunctionInjector(Class<T> cl, MethodGrabber.Args4<A,B,C,D,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D,E,R> FunctionInjector(Class<T> cl, MethodGrabber.Args5<A,B,C,D,E,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <A,B,C,D,E,F,R> FunctionInjector(Class<T> cl, MethodGrabber.Args6<A,B,C,D,E,F,R> func) {
        this(func, InjectContext.INSTANCE);
    }
    public <R> FunctionInjector(MethodGrabber.Args0<R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,R> FunctionInjector(MethodGrabber.Args1<A,R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,R> FunctionInjector(MethodGrabber.Args2<A,B,R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,R> FunctionInjector(MethodGrabber.Args3<A,B,C,R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D,R> FunctionInjector(MethodGrabber.Args4<A,B,C,D,R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D,E,R> FunctionInjector(MethodGrabber.Args5<A,B,C,D,E,R> func) {
        this(func, InjectContext.STATIC);
    }
    public <A,B,C,D,E,F,R> FunctionInjector(MethodGrabber.Args6<A,B,C,D,E,F,R> func) {
        this(func, InjectContext.STATIC);
    }
}
