package lumine.prisma.launch;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;
import lumine.prisma.mapping.NamingEnvironment;
import lumine.prisma.transform.LaunchTweaker;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;


public class Prisma {
    private static LaunchLogWrapper LOGGER = null;
    private static NamingEnvironment obfEnv = null;
    public static NamingEnvironment getObfuscationEnvironment() {
        return obfEnv;
    }

    static final String DEFAULT_TWEAK = "lumine.prisma.transform.DefaultTweaker";
    static File minecraftHome;
    static File assetsDir;
    static File minecraftJar = null;
    static Map<String,Object> blackboard;

    public static void main(String[] args) {
        new Prisma().launch(args);
    }

    // class loader for mods/classes that must be loaded before transformation
    static ClassLoader preClassLoader;
    // class loader for all classes to be transformed
    static LaunchClassLoader classLoader;

    public static LaunchLogWrapper getLogger() {
        return LOGGER;
    }

    List<String> preLogs = new LinkedList<>();

    private URL[] getURLs() {
        String cp = System.getProperty("java.class.path");
        String[] paths = cp.split(File.pathSeparator);
        List<URL> urls = new ArrayList<>(paths.length);

        String sep = File.separatorChar == '\\' ? "\\\\" : "/";
        String notSep = "[^" + sep + "]+";
        for(int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (path.matches(".+" + sep + "versions" + sep + notSep + sep + notSep + "\\.jar")) {
                preLogs.add("skipping " + path);
                minecraftJar = new File(path);
                // don't add minecraft jar to classpath right away
                continue;
            }
            try {
                urls.add(new File(path).toURI().toURL());
            } catch (MalformedURLException e) {
                System.out.println("Skipped loading URL " + path + ":");
                e.printStackTrace();
            }
        }
        return urls.toArray(new URL[0]);
    }

    private Prisma() {
        try {
            preClassLoader = getClass().getClassLoader();
            classLoader = new LaunchClassLoader(getURLs(), preClassLoader);
            blackboard = new HashMap<>();
        } catch (Exception e) {
            LOGGER = LaunchLogWrapper.create();
            for (String preLog : preLogs) {
                Prisma.LOGGER.info(preLog);
            }
            throw e;
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        LOGGER = LaunchLogWrapper.create();
        for (String preLog : preLogs) {
            Prisma.LOGGER.info(preLog);
        }
    }

    private void launch(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        final OptionSpec<String> versionOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        final OptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
        final OptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
        final OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK);
        final OptionSpec<NamingEnvironment> obfuscationOption = parser.accepts("obfuscation").withRequiredArg().withValuesConvertedBy(new ObfuscationEnvironmentConverter());
        final OptionSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        minecraftHome = options.valueOf(gameDirOption);
        assetsDir = options.valueOf(assetsDirOption);
        final String versionName = options.valueOf(versionOption);
        final List<String> tweakClassNames = new ArrayList<>(options.valuesOf(tweakClassOption));
        final List<String> argumentList = new ArrayList<>();

        // This list of names will be interacted with through tweakers. They can append to this list
        // any 'discovered' tweakers from their preferred mod loading mechanism
        // By making this object discoverable and accessible it's possible to perform
        // things like cascading of tweakers
        blackboard.put("TweakClasses", tweakClassNames);

        // This argument list will be constructed from all tweakers. It is visible here so
        // all tweakers can figure out if a particular argument is present, and add it if not
        blackboard.put("ArgumentList", argumentList);

        try {
            File minecraftDir = minecraftJar.getParentFile();
            if (!minecraftDir.exists()) {
                throw new IllegalStateException("Version folder " + minecraftDir.getName() + " does not exist");
            }

            obfEnv = options.valueOf(obfuscationOption);
            if (obfEnv == null) {
                Prisma.getLogger().warn("Obfuscation environment argument not set, inferred to be OFFICIAL");
                obfEnv = NamingEnvironment.OFFICIAL;
            }
            if (obfEnv == NamingEnvironment.INTERMEDIARY) {
                // If the environment is INTERMEDIARY, try to create the intermediate version of Minecraft
                try {
                    Class.forName("net.minecraft.server.MinecraftServer", false, classLoader);
                    throw new IllegalArgumentException("Minecraft is already on the classpath before launch, this is not allowed when in environment INTERMEDIARY");
                } catch (ClassNotFoundException e) {
                    File intermediaryDir = new File(minecraftDir, "intermediary");
                    File intermediaryJar = new File(intermediaryDir, versionName + ".jar");
                    if (!intermediaryJar.exists()) {
                        // Attempt to remap the JAR to intermediary
                        if (!minecraftJar.exists()) {
                            throw new IllegalStateException("JAR " + versionName + ".jar does not exist");
                        }
                        if (!intermediaryDir.exists()) {
                            intermediaryDir.mkdir();
                        }
                        InputStream mappingsStream = classLoader.getResourceAsStream("mappings/mappings.tiny");
                        if (mappingsStream == null) {
                            throw new IllegalStateException("No mappings found. Try adding yarn to the classpath");
                        }
                        BufferedReader mappingsReader = new BufferedReader(new InputStreamReader(mappingsStream));
                        try {
                            TinyRemapper remapper = TinyRemapper.newRemapper()
                                    .withMappings(TinyUtils.createTinyMappingProvider(mappingsReader, "official", "intermediary"))
                                    .build();
                            OutputConsumerPath consumer = new OutputConsumerPath.Builder(intermediaryJar.toPath()).build();
                            consumer.addNonClassFiles(minecraftJar.toPath());
                            remapper.readInputs(minecraftJar.toPath());
                            for (URL url : classLoader.getURLs()) {
                                try {
                                    remapper.readClassPath(Path.of(url.toURI()));
                                } catch (URISyntaxException ex) {
                                    throw new IllegalStateException(ex);
                                }
                            }
                            remapper.apply(consumer);
                            consumer.close();
                            remapper.finish();
                            Prisma.getLogger().info("Created new intermediary JAR for Minecraft version " + versionName);
                        } catch (Exception any) {
                            if (intermediaryJar.exists()) {
                                intermediaryJar.delete();
                            }
                            throw any;
                        }
                    }
                    // Add result to classpath
                    classLoader.addURL(intermediaryJar.toURI().toURL());
                }
            } else {
                // Add official JAR to classpath
                classLoader.addURL(minecraftJar.toURI().toURL());
            }
        } catch (Exception e) {
            Prisma.getLogger().error("Unable to prepare Minecraft for launch", e);
            System.exit(1);
        }

        for (URL url : classLoader.getURLs()) {
            Prisma.getLogger().info(url.toString());
        }

        // This is to prevent duplicates - in case a tweaker decides to add itself or something
        final Set<String> allTweakerNames = new HashSet<>();
        // The 'definitive' list of tweakers
        final List<LaunchTweaker> allTweakers = new ArrayList<>();
        try {
            final List<LaunchTweaker> tweakers = new ArrayList<>(tweakClassNames.size() + 1);
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

            mainMethod.invoke(null, (Object) argumentList.toArray(new String[0]));
        } catch (Exception e) {
            Prisma.getLogger().error("Unable to launch Minecraft", e);
            System.exit(1);
        }
    }

    private static class ObfuscationEnvironmentConverter implements ValueConverter<NamingEnvironment> {

        @Override
        public NamingEnvironment convert(String value) {
            for (NamingEnvironment env : NamingEnvironment.values()) {
                if (env.name().equalsIgnoreCase(value)) {
                    return env;
                }
            }
            throw new IllegalArgumentException("Invalid obfuscation environment '" + value + "' was provided");
        }

        @Override
        public Class<? extends NamingEnvironment> valueType() {
            return NamingEnvironment.class;
        }

        @Override
        public String valuePattern() {
            return null;
        }
    }
}