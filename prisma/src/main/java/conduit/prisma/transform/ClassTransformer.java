package conduit.prisma.transform;

public interface ClassTransformer {
    byte[] transform(String name, String transformedName, byte[] basicClass);
}
