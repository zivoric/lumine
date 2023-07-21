package lumine.bridge.launch;

import lumine.Lumine;
import lumine.LumineConstants;
import lumine.bridge.modification.ModManagerCore;
import lumine.prisma.LogWrapper;
import lumine.prisma.injection.ClassInjector;
import lumine.prisma.launch.LaunchClassLoader;
import lumine.prisma.transform.InjectorTransformer;
import lumine.prisma.transform.LaunchTweaker;
import lumine.util.GameEnvironment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class LumineTweaker implements LaunchTweaker {
	private static final Set<ClassInjector<?>> injectors = new LinkedHashSet<>();
	private static boolean injectorsAllowed = true;

	public static void addInjectors(Collection<ClassInjector<?>> injectors) throws IllegalStateException {
		if (injectorsAllowed) {
			LumineTweaker.injectors.addAll(injectors);
		} else {
			throw new IllegalStateException("Injections cannot be added after launch");
		}
	}
	
	private final List<String> args = new ArrayList<>();

	@Override
	public void acceptOptions(List<String> args, File game, File assets, String version) {
		this.args.addAll(args);
		this.args.add("--version");
		this.args.add(version);
		if (args.contains("--versionType")) {
			this.args.set(args.indexOf("--versionType")+1, "lumine");
		} else {
			this.args.add("--versionType");
			this.args.add("lumine");
		}
		if (!this.args.contains("--lumineEnvironment") || this.args.lastIndexOf("--lumineEnvironment") == this.args.size()-1) {
			throw new IllegalArgumentException("Lumine environment not specified, must be CLIENT or SERVER");
		}
		String envStr = this.args.get(this.args.indexOf("--lumineEnvironment")+1).toUpperCase();
		GameEnvironment environment;
		try {
			environment = GameEnvironment.valueOf(envStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Environment \""+envStr+"\" is invalid, must be CLIENT or SERVER");
		}
		try {
			Lumine.setEnvironment(environment);
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Environment already set at launch. This should never happen!");
		}
		try {
			Lumine.setLogger(new Log4JLogWrapper());
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Main logger already set at launch. This should never happen!");
		}
		LumineConstants.instance(version.substring(0, version.indexOf("-lumine")), game);
		Lumine.getLogger().info("Tweaker class loaded! Running minecraft " + Lumine.getEnvironment().toString().toLowerCase() + " version " + LumineConstants.instance().MINECRAFT_VERSION_NAME);
		Lumine.getLogger().info("Game directory: " + game.toURI());

		ModManagerCore.initialize();
		ModManagerCore loader = ModManagerCore.getInstance();
		loader.prepareMods();
		loader.initializeMods();
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

	@Override
	public String getLaunchTarget() {
		return Lumine.isClient() ? "net.minecraft.client.main.Main" : "net.minecraft.server.MinecraftServer";
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		injectorsAllowed = false;
		classLoader.registerTransformer(InjectorTransformer.class.getName(), injectors);
	}

	private static class Log4JLogWrapper implements LogWrapper {
		private static final String PREFIX = "[Lumine] ";
		private final Logger logger = LogManager.getFormatterLogger("Lumine");

		private void log(Level level, String message, Object... args) {
			logger.log(level, PREFIX + message, args);
		}

		private void log(Level level, String message, Throwable e) {
			logger.log(level, PREFIX + message, e);
		}

		public void info(String message, Object... args) {
			this.log(Level.INFO, message, args);
		}

		public void warn(String message, Object... args) {
			log(Level.WARN, message, args);
		}

		public void warn(String message, Throwable e) {
			log(Level.WARN, message, e);
		}

		public void debug(String message, Object... args) {
			log(Level.DEBUG, message, args);
		}

		public void debug(String message, Throwable e) {
			log(Level.DEBUG, message, e);
		}

		public void error(String message, Object... args) {
			log(Level.ERROR, message, args);
		}

		public void error(String message, Throwable e) {
			log(Level.ERROR, message, e);
		}
	}
}
