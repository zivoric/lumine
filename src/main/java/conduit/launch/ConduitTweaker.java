package conduit.launch;

import conduit.Conduit;
import conduit.ConduitConstants;
import conduit.launch.ConduitTransformer;
import conduit.modification.ModManagementLoader;
import conduit.modification.exception.ModManagementException;
import conduit.util.GameEnvironment;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ConduitTweaker implements ITweaker {
	
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
		if (!this.args.contains("--conduitEnvironment") || this.args.lastIndexOf("--conduitEnvironment") == this.args.size()-1) {
			throw new IllegalArgumentException("Conduit environment not specified, must be CLIENT or SERVER");
		}
		String envStr = this.args.get(this.args.indexOf("--conduitEnvironment")+1).toUpperCase();
		GameEnvironment environment;
		try {
			environment = GameEnvironment.valueOf(envStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Environment \""+envStr+"\" is invalid, must be CLIENT or SERVER");
		}
		try {
			Conduit.setEnvironment(environment);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Environment already set at launch. This should never happen!");
		}
		ConduitConstants.instance(version.substring(0, version.indexOf("-conduit")), game);
		Conduit.log("Tweaker class loaded! Running minecraft " + Conduit.getEnvironment().toString().toLowerCase() + " version " + ConduitConstants.instance().MINECRAFT_VERSION_NAME);
		Conduit.log("Game directory: " + game.toURI());
		try {
			ModManagementLoader loader = ModManagementLoader.create();
			loader.initializeModManager();
			loader.getModManager();
			loader.invoke("prepareMods");
			loader.invoke("initializeMods");
		} catch (ModManagementException | InvocationTargetException e) {
			Conduit.LOGGER.error("Error while initializing mod manager", e);
		}
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

	@Override
	public String getLaunchTarget() {
		return Conduit.isClient() ? "net.minecraft.client.main.Main" : "net.minecraft.server.MinecraftServer";
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer(ConduitTransformer.class.getName());
	}
}
