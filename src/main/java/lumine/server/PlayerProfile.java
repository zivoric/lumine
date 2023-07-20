package lumine.server;

import lumine.entity.Player;

import java.util.UUID;

public interface PlayerProfile {
    String getName();
    UUID getUUID();
    Player getPlayer(PlayerHandler handler);
}
