package conduit.main;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConduitLauncher implements ITweaker {
	
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
		Conduit.log("Tweaker class loaded! Running minecraft " + Conduit.getEnvironment().toString().toLowerCase() + " version " + ConduitConstants.instance().MINECRAFT_VERSION_NAME);
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
		Conduit.log("injecting into class loader");
		classLoader.registerTransformer(ConduitTransformer.class.getName());
	}
}
