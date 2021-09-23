package conduit.launch;

import conduit.Conduit;
import conduit.client.ClientInjects;
import conduit.injection.ClassInjector;
import conduit.injects.CoreInjects;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.LinkedList;
import java.util.List;

public class ConduitTransformer implements IClassTransformer {
	public ConduitTransformer() {
		injectors = new LinkedList<>(new CoreInjects().getInjectors());
		if (Conduit.isClient()) {
			injectors.addAll(new ClientInjects().getInjectors());
		}
	}
	//private ObfuscationMap map = ConduitUtils.getCurrentMap();
	/*private MethodInjector<Main> clInj = new VoidInjector<Main>(Main::main) {
		@InvokeInjection(InjectProperties.Point.START)
		public static void mainStart(String[] args) {
			Conduit.log("Injected!");
		}
	};*/
	private final List<ClassInjector<?>> injectors;
	@Override
	public byte[] transform(String name, String transformedName, byte[] inputClass) {
		return ClassInjector.transformAll(inputClass, injectors);
	}
}
