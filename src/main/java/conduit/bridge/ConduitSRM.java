package conduit.bridge;

import conduit.bridge.command.ConduitCommandManager;
import conduit.main.Conduit;
import conduit.util.ConduitUtils;
import conduit.util.ObfuscationMap;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;

import java.lang.reflect.Field;

public class ConduitSRM extends ServerResourceManager {
    private ObfuscationMap map;
	public ConduitSRM(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment,
			int functionPermissionLevel) {
		super(registryManager, commandEnvironment, functionPermissionLevel);
		Conduit.log("New ConduitSRM created");
		/*InputStream str = getClass().getClassLoader().getResourceAsStream("obfuscationmap-"+ConduitConstants.instance().MINECRAFT_VERSION_NAME+".json");
		if (str!=null) {
			testmap = new ObfuscationMap(str);
		} else {
			Conduit.warn("Resource " + "obfuscationmap-"+ConduitConstants.instance().MINECRAFT_VERSION_NAME+".json" + " could not be found, will not be able to inject");
			testmap = new ObfuscationMap();
		}*/
		map = ConduitUtils.getCurrentMap();
        reflectSetField("commandManager", new ConduitCommandManager(commandEnvironment));
	}
	private void reflectSetField(String name, Object value) {
		try {
			
			String obfName = map.fromDeobf("net/minecraft/resource/ServerResourceManager").fieldToObf(name);
			Field commandField = this.getClass().getSuperclass().getDeclaredField(obfName);
	        commandField.setAccessible(true);
	        commandField.set(this, value);
		} catch (Exception e) {
			Conduit.log("ConduitSRM: Unable to reflect field " + name);
			e.printStackTrace();
		}
	}
}
