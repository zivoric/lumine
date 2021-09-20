package conduit.entity;

import conduit.command.CommandSender;
import conduit.server.PlayerProfile;
import conduit.world.GameMode;

public interface Player extends CommandSender {
    PlayerProfile getProfile();
    GameMode getGameMode();
    boolean setGameMode(GameMode arg);
}
