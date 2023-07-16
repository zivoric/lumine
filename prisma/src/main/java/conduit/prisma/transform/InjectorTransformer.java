package conduit.prisma.transform;

import conduit.prisma.injection.ClassInjector;

public class InjectorTransformer implements ClassTransformer {

	private final Iterable<ClassInjector<?>> injectors;

	public InjectorTransformer(Iterable<ClassInjector<?>> injectors) {
		this.injectors = injectors;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] inputClass) {
		return ClassInjector.transformAll(inputClass, injectors);
	}
}
