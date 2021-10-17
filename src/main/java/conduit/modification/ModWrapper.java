package conduit.modification;

import conduit.Conduit;
import conduit.modification.config.ModConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ModWrapper {
    private final ConduitMod modInstance;
    private final Class<? extends ConduitMod> mainClass;
    private final Class<? extends ClientMod> clientClass;
    private final Class<? extends ServerMod> serverClass;
    public ModWrapper(Class<? extends ConduitMod> modClass, ModConfiguration config) throws ModLoadException {
        mainClass = modClass;
        try {
            Method getClient = ConduitMod.class.getMethod("getClientMod");
            Method getServer = ConduitMod.class.getMethod("getServerMod");
            Constructor<? extends ConduitMod> constr;
            try {
                constr = modClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new ModLoadException("Main mod class " + modClass.getName() + " does not contain a no-argument constructor");
            }
            constr.setAccessible(true);
            modInstance = constr.newInstance();
            clientClass = modInstance.getClientMod();
            serverClass = modInstance.getServerMod();
        } catch (ModLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModLoadException(e.getMessage());
        }
    }
    public ConduitMod getInstance() {
        return modInstance;
    }
    public Class<? extends ConduitMod> getMainClass() {
        return mainClass;
    }
    public Class<? extends ClientMod> getClientClass() {
        return clientClass;
    }
    public Class<? extends ServerMod> getServerClass() {
        return serverClass;
    }
}
