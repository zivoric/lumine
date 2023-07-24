package lumine.util;

import java.io.File;

public class GameProfile {
	private static GameProfile instance;
	
	private GameProfile(String version, File mcDir) {
		instance = this;
		MINECRAFT_VERSION_NAME = version;
		MINECRAFT_DIRECTORY = mcDir;
		MinecraftVersion versionEnum = MinecraftVersion.fromName(version);
		if (versionEnum == null) versionEnum = MinecraftVersion.defaultVersion();
		MINECRAFT_VERSION = versionEnum;
	}
	
	public final String LUMINE_VERSION = "1.0.0";
	public final String MINECRAFT_VERSION_NAME;
	public final File MINECRAFT_DIRECTORY;
	public final MinecraftVersion MINECRAFT_VERSION;

	public static GameProfile instance(String version, File mcDir) {
		return instance==null ? new GameProfile(version, mcDir) : instance;
	}

	public static GameProfile instance() {
		return instance==null ? instance("1.19.4", null) : instance;
	}
}
