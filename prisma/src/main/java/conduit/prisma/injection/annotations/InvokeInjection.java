package conduit.prisma.injection.annotations;

import conduit.prisma.injection.util.InjectProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvokeInjection {
    InjectProperties.Point value();
}
