package conduit.util;

import conduit.main.Conduit;
import conduit.main.ConduitConstants;

import java.io.InputStream;

public class ConduitUtils {
	private static ObfuscationMap currentMap = null;
	private static int isClient = -1;
	
	public static ObfuscationMap createDefaultMap() {
		return createDefaultMap(getVersion());
	}
	public static ObfuscationMap createDefaultMap(MinecraftVersion version) {
		ObfuscationMap map;
		InputStream str = ConduitUtils.class.getClassLoader().getResourceAsStream("obfuscationmap-"+version.getName()+".json");
		if (str!=null) {
			map = new ObfuscationMap(str);
		} else {
			Conduit.warn("Resource " + "obfuscationmap-"+version.getName()+".json" + " could not be found");
			map = new ObfuscationMap();
		}
		return map;
	}
	public static ObfuscationMap createMap(InputStream stream) {
		ObfuscationMap map;
		if (stream!=null) {
			map = new ObfuscationMap(stream);
		} else {
			Conduit.warn("Create map input stream is null");
			map = new ObfuscationMap();
		}
		return map;
	}
	public static ObfuscationMap getCurrentMap() {
		if (currentMap==null)
			currentMap = createDefaultMap();
		return currentMap;
	}
	
	public static MinecraftVersion getVersion() {
		return MinecraftVersion.fromName(ConduitConstants.instance().MINECRAFT_VERSION_NAME);
	}
	
	public static boolean isClient() {
		if (isClient == 0) return false;
		else if (isClient == 1) return true;
		else {
			boolean client;
			try {
				Class.forName("net.minecraft.client.main.Main");
				client = true;
			} catch (ClassNotFoundException e) {
				client = false;
			}
			isClient = client ? 1 : 0;
			return client;
		}
	}
}
