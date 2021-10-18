package conduit;

import net.minecraft.SharedConstants;

import java.io.File;
import java.net.URISyntaxException;

public class ConduitConstants {
	private static ConduitConstants instance;
	
	private ConduitConstants(String version) {
		instance = this;
		MINECRAFT_VERSION_NAME = version;
		File mcDir;
		try {
			mcDir = new File(SharedConstants.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParentFile().getParentFile().getParentFile();
		} catch (URISyntaxException e) {
			mcDir = null;
		}
		MINECRAFT_DIRECTORY = mcDir;
	}
	
	public final String CONDUIT_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME;
	public final File MINECRAFT_DIRECTORY;
	
	public static ConduitConstants instance(String version) {
		return instance==null ? new ConduitConstants(version) : instance;
	}

	public static ConduitConstants instance() {
		return instance==null ? instance(SharedConstants.getGameVersion().getName()) : instance;
	}
}
