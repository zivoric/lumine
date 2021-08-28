package conduit.util;

public enum MinecraftVersion {
	V1_17_1(11701, "1.17.1");
	
	private int numericVersion;
	private String versionName;
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
