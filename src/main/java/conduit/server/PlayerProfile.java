package conduit.server;

import conduit.entity.Player;

import java.util.UUID;

public interface PlayerProfile {
    String getName();
    UUID getUUID();
    Player getPlayer(PlayerHandler handler);
}
