package lumine.prisma.refract.lambda.annotations;

import lumine.prisma.refract.definition.method.InjectPoint;
import lumine.prisma.refract.definition.method.Options;
import lumine.prisma.refract.definition.method.RefractType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefractLambda {
    RefractType type();
    InjectPoint at();
    Options options() default @Options();
}
