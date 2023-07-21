package lumine.bridge.modification;

import lumine.bridge.modification.exception.ModManagementException;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ModManager {
    private Object modManager = null;
    private final Class<?> mmClass;
    private ModManager(Class<?> cl) {
        mmClass = cl;
    }
    private void createModManager() throws ModManagementException {
        if (modManager == null) {
            modManager = invokeStatic("getInstance");
        }
    }
    public Object getModManager() throws ModManagementException {
        createModManager();
        return modManager;
    }

    public static ModManager create() throws ModManagementException {
        try {
            return new ModManager(
                    ClassLoader.getSystemClassLoader().loadClass(ModManagerCore.class.getName())
            );
        } catch (ClassNotFoundException e) {
            throw ModManagementException.create(e);
        }
    }

    public void initialize() throws ModManagementException {
        invokeStatic("initialize");
        createModManager();
    }

    public void prepareMods() {
        try {
            invoke("prepareMods");
        } catch (Exception ignored) {
        }
    }

    public void prepareClientMods() {
        try {
            invoke("prepareClientMods");
        } catch (Exception ignored) {
        }
    }

    public void prepareServerMods() {
        try {
            invoke("prepareServerMods");
        } catch (Exception ignored) {
        }
    }

    public void initializeMods() {
        try {
            invoke("initializeMods");
        } catch (Exception ignored) {
        }
    }

    public void initializeClientMods() {
        try {
            invoke("initializeClientMods");
        } catch (Exception ignored) {
        }
    }

    public void initializeServerMods() {
        try {
            invoke("initializeServerMods");
        } catch (Exception ignored) {
        }
    }

    public void terminateServerMods() {
        try {
            invoke("terminateServerMods");
        } catch (Exception ignored) {
        }
    }

    public void terminateMods() {
        try {
            invoke("terminateMods");
        } catch (Exception ignored) {
        }
    }

    private Object invoke(Object obj, String name, Object... args) throws ModManagementException {
        try {
            Class<?>[] cl = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            Method m = mmClass.getDeclaredMethod(name, cl);
            m.setAccessible(true);
            return m.invoke(obj, args);
        } catch (Exception e) {
            throw ModManagementException.create(e);
        }
    }
    private Object invoke(String name, Object... args) throws ModManagementException {
        return invoke(modManager, name, args);
    }
    private Object invokeStatic(String name, Object... args) throws ModManagementException {
        return invoke(null, name, args);
    }
}
