package conduit.main;

import conduit.util.GameEnvironment;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Conduit implements ITweaker {
	public static final Logger LOGGER = LogManager.getLogger();
	
	private final List<String> args = new ArrayList<>();

	@Override
	public void acceptOptions(List<String> args, File game, File assets, String version) {
		this.args.addAll(args);
		this.args.add("--version");
		this.args.add(version);
		if (args.contains("--versionType")) {
			this.args.set(args.indexOf("--versionType")+1, "conduit");
		} else {
			this.args.add("--versionType");
			this.args.add("conduit");
		}
		new ConduitConstants(version.substring(0, version.indexOf("-conduit")));
		log("Tweaker class loaded! Running minecraft " + getEnvironment().toString().toLowerCase() + " version " + ConduitConstants.instance().MINECRAFT_VERSION_NAME);
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		log("injecting into class loader");
		classLoader.registerTransformer(ConduitTransformer.class.getName());
	}

	public static GameEnvironment getEnvironment() {
		GameEnvironment env;
		try {
			Class.forName("net.minecraft.client.main.Main");
			env = GameEnvironment.CLIENT;
		} catch (ClassNotFoundException e) {
			env = GameEnvironment.SERVER;
		}
		return env;
	}

	public static boolean isClient() {
		return getEnvironment() == GameEnvironment.CLIENT;
	}

	public static void log(String text) {
		LOGGER.info("[Conduit] " + text);
	}
	public static void log(String... text) {
		for (String t : text) {
			log(t);
		}
	}
	public static void warn(String text) {
		LOGGER.warn("[Conduit] " + text);
	}
	public static void warn(String... text) {
		for (String t : text) {
			warn(t);
		}
	}
	public static void error(String text) {
		LOGGER.error("[Conduit] " + text);
	}
	public static void error(String... text) {
		for (String t : text) {
			error(t);
		}
	}
}
