package lumine.entity;

import lumine.server.PlayerProfile;
import lumine.world.GameMode;

public interface Player extends Entity {
    PlayerProfile getProfile();
    GameMode getGameMode();
    boolean setGameMode(GameMode arg);

    String getName();
}
