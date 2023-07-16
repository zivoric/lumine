package conduit.prisma.launch;

import conduit.prisma.transform.ClassTransformer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LaunchClassLoader extends URLClassLoader {
    public static final int BUFFER_SIZE = 1 << 12;
    private final List<URL> sources;
    private final ClassLoader parent = getClass().getClassLoader();

    private final List<ClassTransformer> transformers = new ArrayList<ClassTransformer>(2);
    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<String, Class<?>>();
    private final Set<String> invalidClasses = new HashSet<String>(1000);

    private final Set<String> classLoaderExceptions = new HashSet<String>();
    private final Set<String> classLoaderInclusions = new HashSet<>();
    private final Set<String> transformerExceptions = new HashSet<String>();
    private final Map<Package, Manifest> packageManifests = new ConcurrentHashMap<Package, Manifest>();
    private final Map<String,byte[]> resourceCache = new ConcurrentHashMap<String,byte[]>(1000);
    private final Set<String> negativeResourceCache = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private static final Manifest EMPTY = new Manifest();

    private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal<byte[]>();

    private static final String[] RESERVED_NAMES = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
    private static final boolean DEBUG_FINER = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"));
    private static final boolean DEBUG_SAVE = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false"));
    private static File tempFolder = null;

    public LaunchClassLoader(URL[] sources) {
        super(sources, null);
        this.sources = new ArrayList<URL>(Arrays.asList(sources));

        // classloader inclusions (overrides any exclusions)
        addClassLoaderInclusion("conduit.prisma.injection.");

        // classloader exclusions
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("org.lwjgl.");
        //addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("conduit.prisma.");

        // transformer exclusions
        addTransformerExclusion("javax.");
        addTransformerExclusion("argo.");
        addTransformerExclusion("org.objectweb.asm.");
        addTransformerExclusion("com.google.common.");
        addTransformerExclusion("org.bouncycastle.");
        addTransformerExclusion("conduit.");

        if (DEBUG_SAVE) {
            int x = 1;
            tempFolder = new File(Prisma.minecraftHome, "CLASSLOADER_TEMP");
            while (tempFolder.exists() && x <= 10) {
                tempFolder = new File(Prisma.minecraftHome, "CLASSLOADER_TEMP" + x++);
            }

            if (tempFolder.exists()) {
                Prisma.getLogger().info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.");
                tempFolder = null;
            } else {
                Prisma.getLogger().info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", tempFolder.getAbsolutePath().replace('\\', '/'));
                tempFolder.mkdirs();
            }
        }
    }

    public void registerTransformer(String transformerClassName, Object... args) {
        try {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Constructor<?> targetConstructor = null;
            constructors: for (Constructor<?> constructor : loadClass(transformerClassName).getDeclaredConstructors()) {
                if (constructor.getParameterCount() == args.length) {
                    Class<?>[] params = constructor.getParameterTypes();
                    for (int i = 0; i < args.length; i++) {
                        if (!params[i].isAssignableFrom(types[i])) {
                            continue constructors;
                        }
                    }
                    targetConstructor = constructor;
                    break;
                }
            }

            if (targetConstructor == null) {
                throw new NoSuchMethodException("No constructor in " + transformerClassName + " matches the specified parameters");
            }
            ClassTransformer transformer = (ClassTransformer) targetConstructor.newInstance(args);
            transformers.add(transformer);
        } catch (Exception e) {
            Prisma.getLogger().error("A critical problem occurred registering the ASM transformer class " + transformerClassName, e);
        }
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        if (invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }

        boolean included = false;
        for (final String inclusion : classLoaderInclusions) {
            if (name.startsWith(inclusion)) {
                included = true;
                break;
            }
        }

        if (!included) {
            for (final String exception : classLoaderExceptions) {
                if (name.startsWith(exception)) {
                    return parent.loadClass(name);
                }
            }
        }

        if (cachedClasses.containsKey(name)) {
            return cachedClasses.get(name);
        }

        for (final String exception : transformerExceptions) {
            if (name.startsWith(exception)) {
                try {
                    final Class<?> clazz = super.findClass(name);
                    cachedClasses.put(name, clazz);
                    return clazz;
                } catch (ClassNotFoundException e) {
                    invalidClasses.add(name);
                    throw e;
                }
            }
        }

        try {
            final String transformedName = name;
            if (cachedClasses.containsKey(transformedName)) {
                return cachedClasses.get(transformedName);
            }

            final String untransformedName = name;

            final int lastDot = untransformedName.lastIndexOf('.');
            final String packageName = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
            final String fileName = untransformedName.replace('.', '/').concat(".class");
            URLConnection urlConnection = findCodeSourceConnectionFor(fileName);

            CodeSigner[] signers = null;

            if (lastDot > -1 && !untransformedName.startsWith("net.minecraft.")) {
                if (urlConnection instanceof final JarURLConnection jarURLConnection) {
                    final JarFile jarFile = jarURLConnection.getJarFile();

                    if (jarFile != null && jarFile.getManifest() != null) {
                        final Manifest manifest = jarFile.getManifest();
                        final JarEntry entry = jarFile.getJarEntry(fileName);

                        Package pkg = getDefinedPackage(packageName);
                        getClassBytes(untransformedName);
                        signers = entry.getCodeSigners();
                        if (pkg == null) {
                            pkg = definePackage(packageName, manifest, jarURLConnection.getJarFileURL());
                            packageManifests.put(pkg, manifest);
                        } else {
                            if (pkg.isSealed() && !pkg.isSealed(jarURLConnection.getJarFileURL())) {
                                Prisma.getLogger().error("The jar file %s is trying to seal already secured path %s", jarFile.getName(), packageName);
                            } else if (isSealed(packageName, manifest)) {
                                Prisma.getLogger().error("The jar file %s has a security seal for path %s, but that path is defined and not secure", jarFile.getName(), packageName);
                            }
                        }
                    }
                } else {
                    Package pkg = getDefinedPackage(packageName);
                    if (pkg == null) {
                        pkg = definePackage(packageName, null, null, null, null, null, null, null);
                        packageManifests.put(pkg, EMPTY);
                    } else if (pkg.isSealed()) {
                        Prisma.getLogger().error("The URL %s is defining elements for sealed path %s", urlConnection.getURL(), packageName);
                    }
                }
            }

            final byte[] transformedClass = runTransformers(untransformedName, transformedName, getClassBytes(untransformedName));
            if (DEBUG_SAVE) {
                saveTransformedClass(transformedClass, transformedName);
            }

            final CodeSource codeSource = urlConnection == null ? null : new CodeSource(urlConnection.getURL(), signers);
            final Class<?> clazz = defineClass(transformedName, transformedClass, 0, transformedClass.length, codeSource);
            cachedClasses.put(transformedName, clazz);
            return clazz;
        } catch (Throwable e) {
            invalidClasses.add(name);
            if (DEBUG) {
                Prisma.getLogger().trace("Exception encountered attempting classloading of "+name, e);
            }
            throw new ClassNotFoundException(name, e);
        }
    }

    private void saveTransformedClass(final byte[] data, final String transformedName) {
        if (tempFolder == null) {
            return;
        }

        final File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
        final File outDir = outFile.getParentFile();

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (outFile.exists()) {
            outFile.delete();
        }

        try {
            Prisma.getLogger().info("Saving transformed class \"%s\" to \"%s\"", transformedName, outFile.getAbsolutePath().replace('\\', '/'));

            final OutputStream output = new FileOutputStream(outFile);
            output.write(data);
            output.close();
        } catch (IOException ex) {
            Prisma.getLogger().warn("Could not save transformed class \""+ transformedName + "\"", ex);
        }
    }

    private boolean isSealed(final String path, final Manifest manifest) {
        Attributes attributes = manifest.getAttributes(path);
        String sealed = null;
        if (attributes != null) {
            sealed = attributes.getValue(Name.SEALED);
        }

        if (sealed == null) {
            attributes = manifest.getMainAttributes();
            if (attributes != null) {
                sealed = attributes.getValue(Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    private URLConnection findCodeSourceConnectionFor(final String name) {
        final URL resource = findResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private byte[] runTransformers(final String name, final String transformedName, byte[] basicClass) {
        if (DEBUG_FINER) {
            Prisma.getLogger().info("Beginning transform of {%s (%s)} Start Length: %d", name, transformedName, (basicClass == null ? 0 : basicClass.length));
            for (final ClassTransformer transformer : transformers) {
                final String transName = transformer.getClass().getName();
                Prisma.getLogger().info("Before Transformer {%s (%s)} %s: %d", name, transformedName, transName, (basicClass == null ? 0 : basicClass.length));
                basicClass = transformer.transform(name, transformedName, basicClass);
                Prisma.getLogger().info("After  Transformer {%s (%s)} %s: %d", name, transformedName, transName, (basicClass == null ? 0 : basicClass.length));
            }
            Prisma.getLogger().info("Ending transform of {%s (%s)} Start Length: %d", name, transformedName, (basicClass == null ? 0 : basicClass.length));
        } else {
            for (final ClassTransformer transformer : transformers) {
                basicClass = transformer.transform(name, transformedName, basicClass);
            }
        }
        return basicClass;
    }

    @Override
    public void addURL(final URL url) {
        super.addURL(url);
        sources.add(url);
    }

    public List<URL> getSources() {
        return sources;
    }

    private byte[] readFully(InputStream stream) {
        try {
            byte[] buffer = getOrCreateBuffer();

            int read;
            int totalLength = 0;
            while ((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
                totalLength += read;

                // Extend our buffer
                if (totalLength >= buffer.length - 1) {
                    byte[] newBuffer = new byte[buffer.length + BUFFER_SIZE];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    buffer = newBuffer;
                }
            }

            final byte[] result = new byte[totalLength];
            System.arraycopy(buffer, 0, result, 0, totalLength);
            return result;
        } catch (Throwable t) {
            Prisma.getLogger().warn("Problem loading class", t);
            return new byte[0];
        }
    }

    private byte[] getOrCreateBuffer() {
        byte[] buffer = loadBuffer.get();
        if (buffer == null) {
            loadBuffer.set(new byte[BUFFER_SIZE]);
            buffer = loadBuffer.get();
        }
        return buffer;
    }

    public List<ClassTransformer> getTransformers() {
        return Collections.unmodifiableList(transformers);
    }

    public void addClassLoaderExclusion(String toExclude) {
        classLoaderExceptions.add(toExclude);
    }

    public void addClassLoaderInclusion(String toInclude) {
        classLoaderInclusions.add(toInclude);
    }

    public void addTransformerExclusion(String toExclude) {
        transformerExceptions.add(toExclude);
    }

    public byte[] getClassBytes(String name) throws IOException {
        if (negativeResourceCache.contains(name)) {
            return null;
        } else if (resourceCache.containsKey(name)) {
            return resourceCache.get(name);
        }
        if (name.indexOf('.') == -1) {
            for (final String reservedName : RESERVED_NAMES) {
                if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
                    final byte[] data = getClassBytes("_" + name);
                    if (data != null) {
                        resourceCache.put(name, data);
                        return data;
                    }
                }
            }
        }

        InputStream classStream = null;
        try {
            final String resourcePath = name.replace('.', '/').concat(".class");
            final URL classResource = findResource(resourcePath);

            if (classResource == null) {
                if (DEBUG) Prisma.getLogger().info("Failed to find class resource %s", resourcePath);
                negativeResourceCache.add(name);
                return null;
            }
            classStream = classResource.openStream();

            if (DEBUG) Prisma.getLogger().info("Loading class %s from resource %s", name, classResource.toString());
            final byte[] data = readFully(classStream);
            resourceCache.put(name, data);
            return data;
        } finally {
            closeSilently(classStream);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void clearNegativeEntries(Set<String> entriesToClear) {
        negativeResourceCache.removeAll(entriesToClear);
    }
}
