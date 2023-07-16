package conduit.modification;

import conduit.Conduit;
import conduit.ConduitConstants;
import conduit.modification.config.ModConfiguration;
import conduit.modification.exception.ModLoadException;
import conduit.modification.exception.ModRuntimeException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ModManagerCore {
    private static ModManagerCore instance;

    private File modFolder;
    private List<ModWrapper> mods;

    private ModManagerCore() {}
    public static ModManagerCore getInstance() {
        return instance;
    }
    private void initInstance() {
        Map<String, ModWrapper> modMap = new LinkedHashMap<>();
        File minecraftDir = ConduitConstants.instance().MINECRAFT_DIRECTORY;
        File[] fileArray = null;
        if (minecraftDir != null) {
            modFolder = new File(minecraftDir, "cmods");
            fileArray = modFolder.listFiles();
        }
        if (fileArray == null) fileArray = new File[0];
        List<File> folderContents = new ArrayList<>(Arrays.asList(fileArray));
        folderContents.removeIf(file -> !file.getName().endsWith(".jar"));
        for (File file : folderContents) {
            try {
                URL jarUrl = file.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, ClassLoader.getSystemClassLoader());
                InputStream configStream = loader.getResourceAsStream("mod.json");
                if (configStream == null) {
                    throw new ModLoadException("Mod configuration file not found");
                }
                ModConfiguration config;
                try {
                    config = new ModConfiguration(configStream);
                } catch (IllegalArgumentException e) {
                    throw ModLoadException.create(e);
                }
                Class<?> modClass;
                try {
                    modClass = Class.forName(config.getValues().getModClass(), true, loader);
                    if (!ConduitMod.class.isAssignableFrom(modClass)) {
                        throw new ModLoadException("Main mod class " + config.getValues().getModClass() + " does not extend ConduitMod");
                    }
                } catch (ClassNotFoundException e) {
                    throw new ModLoadException("Main mod class " + config.getValues().getModClass() + " does not exist");
                }
                @SuppressWarnings("unchecked")
                ModWrapper mod = new ModWrapper((Class<? extends ConduitMod>) modClass, config);
                if (modMap.containsKey(mod.getId())) mod.incrementId();
                modMap.put(mod.getId(), mod);
            } catch (Exception e) {
                ModLoadException mle = ModLoadException.create(e);
                Conduit.LOGGER.error("Exception in loading of mod " + file.getName(), mle);
            }
        }
        ArrayList<ModWrapper> mods = new ArrayList<>(modMap.values());
        for (Map.Entry<String, ModWrapper> entry : modMap.entrySet()) {
            ModWrapper mod = entry.getValue();
            ModConfiguration.ConfigValues values = mod.getConfig().getValues();
            String[] dependencies = values.getDependencies();
            for (String dependStr : dependencies) {
                ModWrapper dependency = modMap.get(dependStr);
                if (dependency != null) {
                    mods.remove(dependency);
                    mods.add(mods.indexOf(mod), dependency);
                }
            }
        }
        this.mods = mods;
    }

    public void prepareMods() {
        for (ModWrapper mod : mods) {
            if (!mod.isMainPrepared()) {
                try {
                    mod.prepareMod();
                } catch (ModLoadException e) {
                    Conduit.LOGGER.error("Exception while preparing mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion(), e);
                }
                Conduit.log("Successfully prepared mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }
    void prepareClientMods() {
        for (ModWrapper mod : mods) {
            if (!mod.isClientPrepared()) {
                try {
                    mod.prepareClient();
                } catch (ModLoadException e) {
                    Conduit.LOGGER.error("Exception while preparing client mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion(), e);
                }
                Conduit.log("Successfully prepared client mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }
    void prepareServerMods() {
        for (ModWrapper mod : mods) {
            if (!mod.isServerPrepared()) {
                try {
                    mod.prepareServer();
                } catch (ModLoadException e) {
                    Conduit.LOGGER.error("Exception while preparing server mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion(), e);
                }
                Conduit.log("Successfully prepared server mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }
    public void initializeMods() {
        for (ModWrapper mod : mods) {
            if (mod.isMainPrepared()) {
                try {
                    mod.getInstance().initialize();
                } catch (IllegalStateException ignored) {
                } catch (Exception e) {
                    Conduit.LOGGER.error("Error while initializing mod " + mod.getMainClass().getName(), ModRuntimeException.create(e));
                }
                Conduit.log("Successfully initialized mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }
    void initializeClientMods() {
        for (ModWrapper mod : mods) {
            if (mod.isClientPrepared()) {
                try {
                    mod.getInstance().getClientModInstance().initialize();
                } catch (IllegalStateException ignored) {
                } catch (Exception e) {
                    Conduit.LOGGER.error("Error while initializing client mod " + mod.getClientClass().getName(), ModRuntimeException.create(e));
                }
                Conduit.log("Successfully initialized client mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }
    void initializeServerMods() {
        for (ModWrapper mod : mods) {
            if (mod.isServerPrepared()) {
                try {
                    mod.getInstance().getServerModInstance().initialize();
                } catch (IllegalStateException ignored) {
                } catch (Exception e) {
                    Conduit.LOGGER.error("Error while initializing server mod " + mod.getClientClass().getName(), ModRuntimeException.create(e));
                }
                Conduit.log("Successfully initialized server mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }

    void terminateServerMods() {
        ArrayList<ModWrapper> reversed = new ArrayList<>(mods);
        Collections.reverse(reversed);
        for (ModWrapper mod : reversed) {
            if (mod.isServerPrepared()) {
                try {
                    mod.getServerInstance().terminate();
                } catch (IllegalStateException ignored) {
                } catch (Exception e) {
                    Conduit.LOGGER.error("Error while terminating server mod " + mod.getMainClass().getName(), ModRuntimeException.create(e));
                }
                Conduit.log("Successfully terminated server mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }

    void terminateMods() {
        ArrayList<ModWrapper> reversed = new ArrayList<>(mods);
        Collections.reverse(reversed);
        for (ModWrapper mod : reversed) {
            if (mod.isMainPrepared()) {
                try {
                    mod.getInstance().terminate();
                } catch (IllegalStateException ignored) {
                } catch (Exception e) {
                    Conduit.LOGGER.error("Error while terminating mod " + mod.getMainClass().getName(), ModRuntimeException.create(e));
                }
                Conduit.log("Successfully terminated mod " + mod.getConfig().getValues().getName() + " v" + mod.getConfig().getValues().getVersion());
            }
        }
    }

    List<ModWrapper> getMods() {
        return Collections.unmodifiableList(mods);
    }

    File getModFolder() {
        return modFolder;
    }

    public static void initialize() throws IllegalStateException {
        if (instance != null) {
            throw new IllegalStateException("Mod manager is already initialized");
        }
        instance = new ModManagerCore();
        instance.initInstance();
    }
}
