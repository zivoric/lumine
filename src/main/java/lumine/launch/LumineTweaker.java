package lumine.launch;

import lumine.Lumine;
import lumine.LumineConstants;
import lumine.client.ClientInjects;
import lumine.injects.CoreInjects;
import lumine.modification.ModManagerCore;
import lumine.prisma.injection.ClassInjector;
import lumine.prisma.launch.LaunchClassLoader;
import lumine.prisma.transform.InjectorTransformer;
import lumine.prisma.transform.LaunchTweaker;
import lumine.util.GameEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LumineTweaker implements LaunchTweaker {
	
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
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Environment already set at launch. This should never happen!");
		}
		LumineConstants.instance(version.substring(0, version.indexOf("-lumine")), game);
		Lumine.log("Tweaker class loaded! Running minecraft " + Lumine.getEnvironment().toString().toLowerCase() + " version " + LumineConstants.instance().MINECRAFT_VERSION_NAME);
		Lumine.log("Game directory: " + game.toURI());

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
		final List<ClassInjector<?>> injectors = new LinkedList<>(new CoreInjects().getInjectors());
		if (Lumine.isClient()) {
			injectors.addAll(new ClientInjects().getInjectors());
		}
		classLoader.registerTransformer(InjectorTransformer.class.getName(), injectors);
	}
}
