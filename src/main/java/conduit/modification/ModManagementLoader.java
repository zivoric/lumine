package conduit.modification;

import conduit.Conduit;
import conduit.modification.exception.ModManagementException;
import conduit.modification.exception.ModManagementException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ModManagementLoader {
    private Object modManager = null;
    private final Class<?> mmClass;
    private ModManagementLoader(Class<?> cl) {
        mmClass = cl;
    }
    public Object getModManager() throws ModManagementException {
        try {
            if (modManager == null) {
                modManager = mmClass.getMethod("getInstance").invoke(null);
            }
        } catch (Exception e) {
            throw ModManagementException.create(e);
        }
        return modManager;
    }
    public void initializeModManager() throws ModManagementException, InvocationTargetException {
        try {
            mmClass.getMethod("initialize").invoke(null);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw ModManagementException.create(e);
        }
    }
    private Object invoke(Object obj, String name, Object... args) throws ModManagementException {
        try {
            Class<?>[] cl = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            return mmClass.getMethod(name, cl).invoke(obj, args);
        } catch (Exception e) {
            throw ModManagementException.create(e);
        }
    }
    public Object invoke(String name, Object... args) throws ModManagementException {
        return invoke(modManager, name, args);
    }
    public Object invokeStatic(String name, Object... args) throws ModManagementException {
        return invoke(null, name, args);
    }

    public static ModManagementLoader create() throws ModManagementException {
        try {
            return new ModManagementLoader(
                ClassLoader.getSystemClassLoader().loadClass(ModManager.class.getName())
            );
        } catch (ClassNotFoundException e) {
            throw ModManagementException.create(e);
        }
    }
}
