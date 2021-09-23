package conduit;

import net.minecraft.SharedConstants;

public class ConduitConstants {
	private static ConduitConstants instance;
	
	private ConduitConstants(String version) {
		instance = this;
		MINECRAFT_VERSION_NAME = version;
	}
	
	public final String CONDUIT_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME;
	
	public static ConduitConstants instance(String version) {
		return instance==null ? new ConduitConstants(version) : instance;
	}

	public static ConduitConstants instance() {
		return instance==null ? instance(SharedConstants.getGameVersion().getName()) : instance;
	}
}
