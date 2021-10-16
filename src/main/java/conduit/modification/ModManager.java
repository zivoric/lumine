package conduit.modification;

import conduit.Conduit;
import conduit.modification.config.ModConfiguration;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModManager {
    private static ModManager instance;

    private File modFolder;

    private ModManager() {}
    public static ModManager getInstance() {
        return instance;
    }
    public void initInstance() {
        modFolder = new File(MinecraftClient.getInstance().runDirectory, "cmods");
        File[] fileArray = modFolder.listFiles();
        if (fileArray == null) return;
        List<File> folderContents = new ArrayList<File>(Arrays.asList(fileArray));
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
                    throw new ModLoadException("Failed to load configuration: " + e.getMessage());
                }
                try {
                    Class<?> modClass = Class.forName(config.getValues().getModClass(), true, loader);
                    if (!ConduitMod.class.isAssignableFrom(modClass)) {
                        throw new ModLoadException("Main mod class " + config.getValues().getModClass() + " does not extend ConduitMod");
                    }
                } catch (ClassNotFoundException e) {
                    throw new ModLoadException("Main mod class " + config.getValues().getModClass() + " does not exist");
                }

            } catch (ModLoadException e) {
                Conduit.LOGGER.error("Exception in loading of mod " + file.getName(), e);
            } catch (Exception e) {
                ModLoadException mle = new ModLoadException(e.getClass().getName() + ": " + e.getMessage());
                mle.setStackTrace(e.getStackTrace());
                Conduit.LOGGER.error("Exception in loading of mod " + file.getName(), mle);
            }
        }
    }

    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException("Mod manager is already initialized");
        }
        instance = new ModManager();
        instance.initInstance();
    }
}
