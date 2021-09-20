package conduit.bridge.server;

import com.mojang.authlib.GameProfile;
import conduit.entity.Player;
import conduit.server.PlayerHandler;
import conduit.server.PlayerProfile;

import java.util.UUID;

public class BridgePlayerProfile implements PlayerProfile {
    private final GameProfile profile;

    private final String name;
    private final UUID uuid;

    public BridgePlayerProfile(GameProfile profile) {
        this.profile = profile;
        name = profile.getName();
        uuid = profile.getId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Player getPlayer(PlayerHandler handler) {
        return handler.getPlayer(uuid);
    }
}
