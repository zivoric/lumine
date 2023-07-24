package lumine.prisma.refract.lambda.generic;

public record GenericMethod<T>(Class<T> targetClass, Integer index, String name) {
}
