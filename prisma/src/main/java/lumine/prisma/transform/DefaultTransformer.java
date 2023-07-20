package lumine.prisma.transform;

public class DefaultTransformer implements ClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return basicClass;
    }
}
