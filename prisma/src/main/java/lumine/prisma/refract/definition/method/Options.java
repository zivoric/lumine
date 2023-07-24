package lumine.prisma.refract.definition.method;

public @interface Options {
    boolean cacheValue() default false;
    boolean passInstance() default false;
}
