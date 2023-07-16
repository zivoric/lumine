package conduit.prisma.injection.generic;

public record GenericMethod<T>(Class<T> targetClass, Integer index, String name) {
}
