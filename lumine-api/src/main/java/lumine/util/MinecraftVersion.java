package lumine.util;

public enum MinecraftVersion {
	V1_19_4(11904, "1.19.4");
	
	private final int numericVersion;
	private final String versionName;
	private MinecraftVersion(int numeric, String name) {
		numericVersion = numeric;
		versionName = name;
	}
	public String getName() {
		return versionName;
	}
	public int getNumericVersion() {
		return numericVersion;
	}
	public static MinecraftVersion fromName(String vers) {
		for (MinecraftVersion version : MinecraftVersion.values()) {
			if (version.getName().equals(vers))
				return version;
		}
		return null;
	}
	@Override
	public String toString() {
		return versionName;
	}
}
