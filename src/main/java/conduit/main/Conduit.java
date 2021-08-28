package conduit.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.SharedConstants;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class Conduit implements ITweaker {
	public static final Logger LOGGER = LogManager.getLogger();
	
	private final List<String> args = new ArrayList<>();
	
	private final String MINECRAFT_VERSION_NAME = SharedConstants.VERSION_NAME;
	
	@Override
	public void acceptOptions(List<String> args, File game, File assets, String version) {
		this.args.addAll(args);
		this.args.add("--version");
		this.args.add(version);
		log("Tweaker class loaded! Running minecraft version " + MINECRAFT_VERSION_NAME);
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
		/*MixinBootstrap.init();
		MixinEnvironment.getDefaultEnvironment().setSide(Side.CLIENT);
		MixinEnvironment.getDefaultEnvironment().setObfuscationContext("notch");
		Mixins.addConfiguration("conduit-mixins.json");*/
		classLoader.registerTransformer(ConduitTransformer.class.getName());
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
}
