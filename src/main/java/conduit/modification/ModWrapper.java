package conduit.modification;

import conduit.Conduit;
import conduit.modification.config.ModConfiguration;

import java.lang.reflect.Method;

public class ModWrapper {
    private final Class<? extends ConduitMod> mainClass;
    private final Class<? extends ClientMod> clientClass;
    private final Class<? extends ServerMod> serverClass;
    public ModWrapper(Class<? extends ConduitMod> modClass, ModConfiguration config) throws ModLoadException {
        mainClass = modClass;
        try {
            Method getClient = ConduitMod.class.getMethod("getClientMod");
            Method getServer = ConduitMod.class.getMethod("getServerMod");

        } catch (Exception e) {
            throw new ModLoadException(e.getMessage());
        }
    }
}
