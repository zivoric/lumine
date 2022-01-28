package conduit;

import net.minecraft.SharedConstants;

import java.io.File;
import java.net.URISyntaxException;

public class ConduitConstants {
	private static ConduitConstants instance;
	
	private ConduitConstants(String version, File mcDir) {
		instance = this;
		MINECRAFT_VERSION_NAME = version;
		MINECRAFT_DIRECTORY = mcDir;
	}
	
	public final String CONDUIT_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME;
	public final File MINECRAFT_DIRECTORY;

	public static ConduitConstants instance(String version, File mcDir) {
		return instance==null ? new ConduitConstants(version, mcDir) : instance;
	}

	public static ConduitConstants instance(String version) {
		return instance(version, null);
	}

	public static ConduitConstants instance() {
		return instance==null ? instance(SharedConstants.getGameVersion().getName(), null) : instance;
	}
}
