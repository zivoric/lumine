package conduit.entity;

import conduit.server.PlayerProfile;
import conduit.world.GameMode;

public interface Player extends Entity {
    PlayerProfile getProfile();
    GameMode getGameMode();
    boolean setGameMode(GameMode arg);
}
