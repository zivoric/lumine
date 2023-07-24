package lumine.prisma.refract.lambda.annotations.types;

import lumine.prisma.refract.definition.method.InjectPoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvokeInjection {
    InjectPoint value();
}
