package conduit.main;

import net.minecraft.SharedConstants;

public class ConduitConstants {
	private static ConduitConstants instance;
	
	private ConduitConstants() {
		instance = this;
	}
	
	public final String CONDUIT_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME = SharedConstants.VERSION_NAME;
	
	public static ConduitConstants instance() {
		return instance==null ? new ConduitConstants() : instance;
	}
}
