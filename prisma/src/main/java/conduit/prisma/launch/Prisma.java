package conduit.prisma.launch;

import conduit.prisma.transform.LaunchTweaker;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Prisma {
    private static LogWrapper LOGGER = null;
    private static final String DEFAULT_TWEAK = "conduit.prisma.transform.DefaultTweaker";
    public static File minecraftHome;
    public static File assetsDir;
    public static Map<String,Object> blackboard;

    public static void main(String[] args) {
        new Prisma().launch(args);
    }

    // class loader for mods/classes that must be loaded before transformation
    public static ClassLoader preClassLoader;
    // class loader for all classes to be transformed
    public static LaunchClassLoader classLoader;

    public static LogWrapper getLogger() {
        return LOGGER;
    }

    private URL[] getURLs() {
        String cp = System.getProperty("java.class.path");
        String[] paths = cp.split(File.pathSeparator);
        URL[] urls = new URL[paths.length];

        for(int i = 0; i < paths.length; i++) {
            String path = paths[i];
            try {urls[i] = (new File(path)).toURI().toURL();
            } catch (MalformedURLException e) {
                System.out.println("Skipped loading URL " + path + ":");
                e.printStackTrace();
            }
        }
        return urls;
    }

    private Prisma() {
        preClassLoader = getClass().getClassLoader();
        classLoader = new LaunchClassLoader(getURLs());
        blackboard = new HashMap<>();
        Thread.currentThread().setContextClassLoader(classLoader);
        LOGGER = LogWrapper.create();
        //LOGGER = LogWrapper.create(LogManager.getFormatterLogger("Prisma"));
    }

    private void launch(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        final OptionSpec<String> versionOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        final OptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
        final OptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
        final OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK);
        final OptionSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        minecraftHome = options.valueOf(gameDirOption);
        assetsDir = options.valueOf(assetsDirOption);
        final String versionName = options.valueOf(versionOption);
        final List<String> tweakClassNames = new ArrayList<String>(options.valuesOf(tweakClassOption));
        final List<String> argumentList = new ArrayList<String>();

        // This list of names will be interacted with through tweakers. They can append to this list
        // any 'discovered' tweakers from their preferred mod loading mechanism
        // By making this object discoverable and accessible it's possible to perform
        // things like cascading of tweakers
        blackboard.put("TweakClasses", tweakClassNames);

        // This argument list will be constructed from all tweakers. It is visible here so
        // all tweakers can figure out if a particular argument is present, and add it if not
        blackboard.put("ArgumentList", argumentList);

        // This is to prevent duplicates - in case a tweaker decides to add itself or something
        final Set<String> allTweakerNames = new HashSet<String>();
        // The 'definitive' list of tweakers
        final List<LaunchTweaker> allTweakers = new ArrayList<LaunchTweaker>();
        try {
            final List<LaunchTweaker> tweakers = new ArrayList<LaunchTweaker>(tweakClassNames.size() + 1);
            // The list of tweak instances - may be useful for interoperability
            blackboard.put("Tweaks", tweakers);
            // The primary tweaker (the first one specified on the command line) will actually
            // be responsible for providing the 'main' name and generally gets called first
            LaunchTweaker primaryTweaker = null;
            // This loop will terminate, unless there is some sort of pathological tweaker
            // that reinserts itself with a new identity every pass
            // It is here to allow tweakers to "push" new tweak classes onto the 'stack' of
            // tweakers to evaluate allowing for cascaded discovery and injection of tweakers
            do {
                for (final Iterator<String> it = tweakClassNames.iterator(); it.hasNext(); ) {
                    final String tweakName = it.next();
                    // Safety check - don't reprocess something we've already visited
                    if (allTweakerNames.contains(tweakName)) {
                        Prisma.getLogger().warn( "Tweak class name %s has already been visited -- skipping", tweakName);
                        // remove the tweaker from the stack otherwise it will create an infinite loop
                        it.remove();
                        continue;
                    } else {
                        allTweakerNames.add(tweakName);
                    }
                    Prisma.getLogger().info("Loading tweak class name %s", tweakName);

                    // Ensure we allow the tweak class to load with the parent classloader
                    classLoader.addClassLoaderExclusion(tweakName.substring(0,tweakName.lastIndexOf('.')));
                    final LaunchTweaker tweaker = (LaunchTweaker) Class.forName(tweakName, true, classLoader).getDeclaredConstructor().newInstance();
                    tweakers.add(tweaker);

                    // Remove the tweaker from the list of tweaker names we've processed this pass
                    it.remove();
                    // If we haven't visited a tweaker yet, the first will become the 'primary' tweaker
                    if (primaryTweaker == null) {
                        Prisma.getLogger().info( "Using primary tweak class name %s", tweakName);
                        primaryTweaker = tweaker;
                    }
                }

                // Now, iterate all the tweakers we just instantiated
                for (final Iterator<LaunchTweaker> it = tweakers.iterator(); it.hasNext(); ) {
                    final LaunchTweaker tweaker = it.next();
                    Prisma.getLogger().info("Calling tweak class %s", tweaker.getClass().getName());
                    tweaker.acceptOptions(options.valuesOf(nonOption), minecraftHome, assetsDir, versionName);
                    tweaker.injectIntoClassLoader(classLoader);
                    allTweakers.add(tweaker);
                    // again, remove from the list once we've processed it, so we don't get duplicates
                    it.remove();
                }
                // continue around the loop until there's no tweak classes
            } while (!tweakClassNames.isEmpty());

            // Once we're done, we then ask all the tweakers for their arguments and add them all to the
            // master argument list
            for (final LaunchTweaker tweaker : allTweakers) {
                argumentList.addAll(Arrays.asList(tweaker.getLaunchArguments()));
            }

            // Finally we turn to the primary tweaker, and let it tell us where to go to launch
            final String launchTarget = primaryTweaker.getLaunchTarget();
            final Class<?> clazz = Class.forName(launchTarget, false, classLoader);
            final Method mainMethod = clazz.getMethod("main", String[].class);

            Prisma.getLogger().info("Launching minecraft with main class {%s}", launchTarget);
            mainMethod.invoke(null, (Object) argumentList.toArray(new String[0]));
        } catch (Exception e) {
            Prisma.getLogger().error("Unable to launch", e);
            System.exit(1);
        }
    }
}