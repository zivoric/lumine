package lumine;

import java.io.File;

public class LumineConstants {
	private static LumineConstants instance;
	
	private LumineConstants(String version, File mcDir) {
		instance = this;
		MINECRAFT_VERSION_NAME = version;
		MINECRAFT_DIRECTORY = mcDir;
	}
	
	public final String LUMINE_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME;
	public final File MINECRAFT_DIRECTORY;

	public static LumineConstants instance(String version, File mcDir) {
		return instance==null ? new LumineConstants(version, mcDir) : instance;
	}

	public static LumineConstants instance() {
		return instance==null ? instance("1.19.4", null) : instance;
	}
}
