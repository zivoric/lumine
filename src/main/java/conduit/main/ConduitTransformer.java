package conduit.main;

import conduit.injection.ClassInjector;
import conduit.injects.CoreInjectors;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.List;

public class ConduitTransformer implements IClassTransformer {
	//private ObfuscationMap map = ConduitUtils.getCurrentMap();
	/*private MethodInjector<Main> clInj = new VoidInjector<Main>(Main::main) {
		@InvokeInjection(InjectProperties.Point.START)
		public static void mainStart(String[] args) {
			Conduit.log("Injected!");
		}
	};*/
	private final List<ClassInjector<?>> injectors = new CoreInjectors().getInjectors();
	@Override
	public byte[] transform(String name, String transformedName, byte[] inputClass) {
		return ClassInjector.transformAll(inputClass, injectors);
	}
}
