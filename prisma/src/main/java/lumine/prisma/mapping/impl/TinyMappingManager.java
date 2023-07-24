package lumine.prisma.mapping.impl;

import com.google.common.io.Files;
import lumine.prisma.launch.LaunchClassLoader;
import lumine.prisma.launch.Prisma;
import lumine.prisma.mapping.MappingManager;
import lumine.prisma.mapping.NamingEnvironment;
import lumine.prisma.refract.MethodInfo;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import net.fabricmc.tinyremapper.api.TrRemapper;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;

public class TinyMappingManager implements MappingManager {
    private final HashMap<String, TinyRemapper> remappers = new HashMap<>();
    private final boolean fromResource;
    private String resource;
    private File file;
    private BufferedReader currentReader;


    public TinyMappingManager() {
        this("mappings/mappings.tiny");
    }

    public TinyMappingManager(String resource) {
        this(getMappingsReader(resource));
    }

    private static BufferedReader getMappingsReader(String resource) {
        InputStream mappingsStream = TinyMappingManager.class.getClassLoader().getResourceAsStream(resource);
        if (mappingsStream == null) {
            throw new IllegalStateException("No tiny mappings found. Try adding yarn to the classpath");
        }
        return new BufferedReader(new InputStreamReader(mappingsStream));
    }

    public TinyMappingManager(File file) {
        this.file = file;
        try {
            currentReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File " + file.getAbsolutePath() + " is invalid", e);
        }
        this.fromResource = false;
    }

    private TinyMappingManager(BufferedReader mappingsReader) {
        currentReader = mappingsReader;
        this.fromResource = true;
    }

    @Override
    public boolean remap(NamingEnvironment from, NamingEnvironment to, File input, File output) {
        try {
            if (from == to) {
                Files.copy(input, output);
            } else {
                TinyRemapper remapper = getRemapper(from, to);
                OutputConsumerPath consumer = new OutputConsumerPath.Builder(output.toPath()).build();
                consumer.addNonClassFiles(input.toPath());
                remapper.readInputs(input.toPath());
                for (URL url : ((LaunchClassLoader) Thread.currentThread().getContextClassLoader()).getURLs()) {
                    try {
                        remapper.readClassPath(Path.of(url.toURI()));
                    } catch (URISyntaxException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
                remapper.apply(consumer);
                consumer.close();
                remapper.finish();
                Prisma.getLogger().info("Remapped file " + input.getAbsolutePath() + " from environment " + from + " to " + to);
                Prisma.getLogger().debug("Output location: " + output.getAbsolutePath());
            }
            return true;
        } catch (IOException e) {
            Prisma.getLogger().error("Error while remapping " + input.getAbsolutePath() + " to " + output.getAbsolutePath(), e);
            if (output.exists()) {
                output.delete();
            }
            return false;
        }
    }

    @Override
    public String remapClassName(NamingEnvironment from, NamingEnvironment to, String inputName) {
        if (from == to) {
            return inputName;
        } else {
            TinyRemapper remapper = getRemapper(from, to);
            return remapper.getEnvironment().getRemapper().map(inputName);
        }
    }

    @Override
    public String remapMethodName(NamingEnvironment from, NamingEnvironment to, MethodInfo info) {
        if (from == to) {
            return info.name();
        } else {
            TinyRemapper remapper = getRemapper(from, to);
            return remapper.getEnvironment().getRemapper().mapMethodName(info.owner(), info.name(), info.desc());
        }
    }

    @Override
    public MethodInfo remapMethodInfo(NamingEnvironment from, NamingEnvironment to, MethodInfo info) {
        if (from == to) {
            return info;
        } else {
            TrRemapper remapper = getRemapper(from, to)
                    .getEnvironment().getRemapper();
            return new MethodInfo(
                    remapper.map(info.owner()),
                    remapper.mapMethodName(info.owner(), info.name(), info.desc()),
                    remapper.mapMethodDesc(info.desc())
            );
        }
    }

    private TinyRemapper getRemapper(NamingEnvironment from, NamingEnvironment to) {
        if (from == to) {
            throw new IllegalArgumentException("Source and destination naming environments must not be the same");
        }
        TinyRemapper remapper = remappers.get(getIdentifier(from, to));
        if (remapper == null) {
            remapper = TinyRemapper.newRemapper()
                    .withMappings(TinyUtils.createTinyMappingProvider(currentReader, tinyEnvironment(from), tinyEnvironment(to)))
                    .build();
            if (this.fromResource) {
                currentReader = getMappingsReader(resource);
            } else {
                try {
                    currentReader = new BufferedReader(new FileReader(file));
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("Mappings file is no longer accessible", e);
                }
            }
        }
        return remapper;
    }

    private static String tinyEnvironment(NamingEnvironment env) {
        switch (env) {

            case OFFICIAL -> {
                return "official";
            }
            case YARN_INTERMEDIARY -> {
                return "intermediary";
            }
            case YARN_NAMED -> {
                return "named";
            }
            default -> {
                throw new IllegalArgumentException("Tiny remapper cannot remap with environment " + env);
            }
        }
    }

    private static String getIdentifier(NamingEnvironment from, NamingEnvironment to) {
        return from.name() + "-" + to.name();
    }
}
