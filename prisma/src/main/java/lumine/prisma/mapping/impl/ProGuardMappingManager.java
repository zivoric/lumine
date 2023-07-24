package lumine.prisma.mapping.impl;

import com.google.common.io.Files;
import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.classprovider.ClassProvider;
import cuchaz.enigma.classprovider.CombiningClassProvider;
import cuchaz.enigma.classprovider.JarClassProvider;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.MappingOperations;
import cuchaz.enigma.translation.mapping.serde.MappingFileNameFormat;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;
import cuchaz.enigma.translation.mapping.serde.MappingSaveParameters;
import cuchaz.enigma.translation.mapping.serde.proguard.ProguardMappingsReader;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.translation.mapping.tree.EntryTreeNode;
import cuchaz.enigma.translation.representation.MethodDescriptor;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;
import lumine.prisma.launch.LaunchClassLoader;
import lumine.prisma.launch.Prisma;
import lumine.prisma.mapping.MappingManager;
import lumine.prisma.mapping.NamingEnvironment;
import lumine.prisma.refract.MethodInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;

public class ProGuardMappingManager implements MappingManager {
    private final Path mappingsPath;
    private final Enigma enigma;
    private final ClassProvider provider;
    private EntryTree<EntryMapping> fromNamed = null;
    private EntryTree<EntryMapping> toNamed = null;

    public ProGuardMappingManager(Path mappingsPath) {
        try {
            enigma = Enigma.create();
            LaunchClassLoader loader = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();
            LinkedList<ClassProvider> providers = new LinkedList<>();
            for (URL url : loader.getURLs()) {
                providers.add(new JarClassProvider(Path.of(url.toURI())));
            }
            provider = new CombiningClassProvider(providers.toArray(new ClassProvider[0]));
            this.mappingsPath = mappingsPath;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create ProGuard map manager", e);
        }
    }

    @Override
    public boolean remap(NamingEnvironment from, NamingEnvironment to, File input, File output) {
        try {
            if (from == to) {
                Files.copy(input, output);
            } else {
                Path inputPath = input.toPath();
                Path outputPath = output.toPath();
                EntryTree<EntryMapping> entries = getMappings(from, to);
                EnigmaProject project = enigma.openJar(inputPath, provider, ProgressListener.none());
                project.setMappings(entries);
                project.exportRemappedJar(ProgressListener.none())
                        .write(outputPath, ProgressListener.none());
            }
            return true;
        } catch (Exception e) {
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
            EntryTree<EntryMapping> mappings = getMappings(from, to);
            EntryMapping mapping = mappings.get(new ClassEntry(inputName));
            return mapping.targetName();
        }
    }

    @Override
    public String remapMethodName(NamingEnvironment from, NamingEnvironment to, MethodInfo info) {
        return remapMethodName(from, to, new MethodEntry(
                new ClassEntry(info.owner()),
                info.name(),
                new MethodDescriptor(info.desc())
        ));
    }

    private String remapMethodName(NamingEnvironment from, NamingEnvironment to, MethodEntry entry) {
        if (from == to) {
            return entry.getName();
        } else {
            EntryTree<EntryMapping> mappings = getMappings(from, to);
            EntryMapping mapping = mappings.get(entry);
            return mapping.targetName();
        }
    }

    private String remapMethodDesc(NamingEnvironment from, NamingEnvironment to, MethodInfo info) {
        String[] fromArgs = info.stringArgs();
        StringBuilder builder = new StringBuilder("(");
        for (String arg : fromArgs) {
            remapInternalType(from, to, builder, arg);
        }
        builder.append(')');
        remapInternalType(from, to, builder, info.strType());
        return builder.toString();
    }

    private void remapInternalType(NamingEnvironment from, NamingEnvironment to, StringBuilder builder, String type) {
        if (type.charAt(0) == 'L') {
            String fromName = type.substring(1,type.length()-1);
            builder.append('L');
            builder.append(remapClassName(from, to, fromName));
            builder.append(';');
        } else if (type.charAt(0) == '[') {
            String fromName = type.substring(1);
            builder.append('[');
            builder.append(remapClassName(from, to, fromName));
        }
    }

    @Override
    public MethodInfo remapMethodInfo(NamingEnvironment from, NamingEnvironment to, MethodInfo info) {
        if (from == to) {
            return info;
        } else {
            String name = remapMethodName(from, to, info);
            String desc = remapMethodDesc(from, to, info);
            return new MethodInfo(name, desc);
        }
    }

    private EntryTree<EntryMapping> getMappings(NamingEnvironment from, NamingEnvironment to) {
        if (from == to) {
            throw new IllegalArgumentException("Source and destination naming environments must not be the same");
        }
        boolean toNamed;
        if (from == NamingEnvironment.OFFICIAL && to == NamingEnvironment.OFFICIAL_NAMED) {
            toNamed = true;
        } else if (from == NamingEnvironment.OFFICIAL_NAMED && to == NamingEnvironment.OFFICIAL) {
            toNamed = false;
        } else {
            throw new IllegalArgumentException("ProGuard remapper cannot map with environments from=" + from + ", to=" + to
                    + " (must be " + NamingEnvironment.OFFICIAL + " or " + NamingEnvironment.OFFICIAL_NAMED + ")");
        }
        if ((toNamed && this.toNamed == null) || (!toNamed && this.fromNamed == null)) {
            try {
                MappingSaveParameters saveParameters = new MappingSaveParameters(toNamed ? MappingFileNameFormat.BY_DEOBF : MappingFileNameFormat.BY_OBF);
                EntryTree<EntryMapping> mojangMappings = ProguardMappingsReader.INSTANCE.read(mappingsPath, ProgressListener.none(), saveParameters);
                this.fromNamed = mojangMappings;
                if (toNamed) {
                    this.toNamed = MappingOperations.invert(mojangMappings);
                }
            } catch (MappingParseException | IOException e) {
                throw new IllegalStateException("Error while retrieving Mojang mappings", e);
            }
        }
        return toNamed ? this.toNamed : this.fromNamed;
    }
}
