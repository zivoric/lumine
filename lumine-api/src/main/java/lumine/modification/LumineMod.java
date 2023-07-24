package lumine.modification;

import lumine.Lumine;

public abstract class LumineMod {
    private ServerMod serverMod;
    private ClientMod clientMod;

    private ModConfiguration config;
    private String modId;

    private boolean enabled = false;

    final void initialize() throws IllegalStateException {
        if (enabled) throw new IllegalStateException("Mod already initialized");
        start();
        enabled = true;
    }
    final void terminate() throws IllegalStateException {
        if (!enabled) throw new IllegalStateException("Mod is not initialized");
        try {
            if (serverMod != null) {
                serverMod.terminate();
            }
        } catch (IllegalStateException e) {
            Lumine.getLogger().warn("Server mod for " + modId + " is already disabled on termination.");
        }
        try {
            if (clientMod != null) {
                clientMod.terminate();
            }
        } catch (IllegalStateException e) {
            Lumine.getLogger().warn("Client mod for " + modId + " is already disabled on termination.");
        }
        stop();
        enabled = false;
    }



    protected abstract void start();
    protected abstract void stop();

    public Class<? extends ClientMod> getClientMod() {
        return null;
    }
    public Class<? extends ServerMod> getServerMod() {
        return null;
    }

    public final ModConfiguration getConfig() {
        return config;
    }
    public final String getId() {
        if (modId == null || modId.isEmpty()) modId = config.getValidId();
        return modId;
    }
    public final ServerMod getServerModInstance() {
        return serverMod;
    }
    public final ClientMod getClientModInstance() {
        return clientMod;
    }
    public final boolean isEnabled() {
        return enabled;
    }

    final void setConfig(ModConfiguration config) {
        this.config = config;
    }
    final void setId(String id) {
        modId = id;
    }

    final void setServerMod(ServerMod mod) {
        serverMod = mod;
        mod.setParent(this);
    }
    final void setClientMod(ClientMod mod) {
        clientMod = mod;
        mod.setParent(this);
    }


}
