package lumine.bridge.launch;

import lumine.bridge.injects.CoreInjects;
import lumine.prisma.launch.LaunchClassLoader;
import lumine.prisma.transform.InjectorTransformer;
import lumine.prisma.transform.LaunchTweaker;
import lumine.prisma.utils.GameEnvironment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class LumineTweaker implements LaunchTweaker {
	protected static final Set<String> injectors = new LinkedHashSet<>();
	protected static boolean injectorsAllowed = true;

	public static void addInjectors(Collection<String> injectors) throws IllegalStateException {
		if (injectorsAllowed) {
			LumineTweaker.injectors.addAll(injectors);
		} else {
			throw new IllegalStateException("Injections cannot be added after launch");
		}
	}

	private final List<String> args = new ArrayList<>();
	private File gameDir;
	private String gameVersion;

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

		gameDir = game;
		gameVersion = version;
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

	@Override
	public String getLaunchTarget() {
		return GameEnvironment.isClient() ? "net.minecraft.client.main.Main" : "net.minecraft.server.MinecraftServer";
	}

	private void setup(LaunchClassLoader loader, List<String> args, File game, String version) {
		try {
			Class<?> cl = loader.loadClass("lumine.bridge.launch.LumineSetup");
			Constructor<?> constr = cl.getDeclaredConstructor();
			constr.setAccessible(true);
			Object setup = constr.newInstance();
			Method m = cl.getDeclaredMethod("setup", List.class, File.class, String.class);
			m.setAccessible(true);
			m.invoke(setup, args, game, version);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("LumineSetup class was not found by classloader", e);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			throw new IllegalStateException("Unable to invoke Lumine setup", e);
		}
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		injectWithDefinitions(classLoader, "");
	}

	protected void injectWithDefinitions(LaunchClassLoader classLoader, Set<String> injectors) {
		setup(classLoader, args, gameDir, gameVersion);
		addInjectors(injectors);
		injectorsAllowed = false;
		classLoader.registerTransformer(InjectorTransformer.class.getName(), injectors);
	}
}
