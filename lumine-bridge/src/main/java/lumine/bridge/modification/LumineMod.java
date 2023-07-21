package lumine.bridge.modification;

import lumine.Lumine;
import lumine.bridge.modification.config.ModConfiguration;
import lumine.util.IDKey;

import java.util.Locale;

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
        if (modId == null || modId.isEmpty()) modId = getValidId(config);
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

    static String getValidId(ModConfiguration config) {
        if (config == null) {
            Lumine.getLogger().error("Exception while retrieving valid id", new IllegalStateException("Configuration must be set to retrieve id"));
            return "";
        }
        String baseId = config.getValues().getId().toLowerCase(Locale.ROOT);
        if (baseId.isEmpty()) {
            baseId = config.getValues().getName();
        }
        char[] chars = baseId.toCharArray();
        StringBuilder validId = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String charString = c + "";
            Character finalChar = null;
            if (!charString.matches(IDKey.KEY_ALLOWED)) {
                if (validId.length() > 0 && validId.charAt(validId.length()-1) != '_') {
                    finalChar = '_';
                }
            } else {
                finalChar = c;
            }
            if (finalChar != null) {
                if (finalChar != '_' || (validId.length() > 0 && i != chars.length - 1)) {
                    validId.append(finalChar);
                }
            }
        }
        return validId.toString();
    }
}
