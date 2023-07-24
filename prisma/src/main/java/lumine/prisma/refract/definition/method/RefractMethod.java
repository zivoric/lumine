package lumine.prisma.refract.definition.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefractMethod {
    String name();
    RefractType type();
    InjectPoint at();
    Options options() default @Options();
}
