package lumine.bridge.modification;

import lumine.bridge.modification.config.ModConfiguration;
import lumine.bridge.modification.exception.ModLoadException;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModWrapper {
    private String id;
    private boolean modPrepared, clientPrepared, serverPrepared = false;
    private boolean hasClientMod, hasServerMod = false;
    private final ModConfiguration config;
    private LumineMod modInstance;
    private ClientMod clientInstance = null;
    private ServerMod serverInstance = null;
    private final Class<? extends LumineMod> mainClass;
    private Class<? extends ClientMod> clientClass;
    private Class<? extends ServerMod> serverClass;
    private final Constructor<? extends LumineMod> mainConstr;
    private Constructor<? extends ClientMod> clientConstr;
    private Constructor<? extends ServerMod> serverConstr;
    public ModWrapper(Class<? extends LumineMod> modClass, ModConfiguration config) throws ModLoadException {
        mainClass = modClass;
        this.config = config;
        id = LumineMod.getValidId(config);
        try {
            try {
                mainConstr = modClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new ModLoadException("Main mod class " + modClass.getName() + " does not contain a no-argument constructor");
            }
            mainConstr.setAccessible(true);
        } catch (Exception e) {
            throw ModLoadException.create(e);
        }
    }

    public LumineMod prepareMod() throws ModLoadException {
        try {
            if (modInstance != null) throw new ModLoadException("Mod is already prepared");
            modInstance = mainConstr.newInstance();
            clientClass = modInstance.getClientMod();
            serverClass = modInstance.getServerMod();
            hasClientMod = clientClass != null;
            hasServerMod = serverClass != null;
            modInstance.setConfig(config);
            modInstance.setId(id);
            if (hasClientMod) {
                try {
                    clientConstr = clientClass.getDeclaredConstructor();
                    clientConstr.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new ModLoadException("Client mod class " + clientClass.getName() + " does not contain a no-argument constructor");
                }
            }
            if (hasServerMod) {
                try {
                    serverConstr = serverClass.getDeclaredConstructor();
                    serverConstr.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new ModLoadException("Server mod class " + serverClass.getName() + " does not contain a no-argument constructor");
                }
            }
            modPrepared = true;
            return modInstance;
        } catch (Exception e) {
            throw ModLoadException.create(e);
        }
    }

    public ClientMod prepareClient() throws ModLoadException {
        if (hasClientMod) {
            try {
                if (clientConstr == null) throw new ModLoadException("Must prepare mod before preparing client");
                if (clientInstance != null) throw new ModLoadException("Client is already prepared");
                clientInstance = clientConstr.newInstance();
                modInstance.setClientMod(clientInstance);
                clientPrepared = true;
            } catch (Exception e) {
                throw ModLoadException.create(e);
            }
        }
        return clientInstance;
    }
    public ServerMod prepareServer() throws ModLoadException {
        if (hasServerMod) {
            try {
                if (serverConstr == null) throw new ModLoadException("Must prepare mod before preparing server");
                if (serverInstance != null) throw new ModLoadException("Server is already prepared");
                serverInstance = serverConstr.newInstance();
                modInstance.setServerMod(serverInstance);
                serverPrepared = true;
            } catch (Exception e) {
                throw ModLoadException.create(e);
            }
        }
        return serverInstance;
    }

    public LumineMod getInstance() {
        return modInstance;
    }
    public ServerMod getServerInstance() {
        return serverInstance;
    }
    public ClientMod getClientInstance() {
        return clientInstance;
    }

    public Class<? extends LumineMod> getMainClass() {
        return mainClass;
    }
    public Class<? extends ClientMod> getClientClass() {
        return clientClass;
    }
    public Class<? extends ServerMod> getServerClass() {
        return serverClass;
    }

    public ModConfiguration getConfig() {
        return config;
    }

    public String getId() {
        return id;
    }
    void incrementId() {
        Pattern p = Pattern.compile("_[0-9]+");
        Matcher m = p.matcher(id);
        if (m.find()) {
            String group = m.group(m.groupCount());
            int num = Integer.parseInt(group.substring(1))+1;
            id = id.substring(0, id.length()-group.length()) + "_" + num;
        } else {
            id = id + "_1";
        }
    }

    public boolean isPrepared() {
        return modPrepared && clientPrepared && serverPrepared;
    }
    public boolean isMainPrepared() {
        return modPrepared;
    }
    public boolean isClientPrepared() {
        return clientPrepared;
    }
    public boolean isServerPrepared() {
        return serverPrepared;
    }
}
