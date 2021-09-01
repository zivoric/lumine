package conduit.server;

import conduit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerHandler {
	Server getServer();
	List<Player> getPlayers();
	Player getPlayer(UUID uuid);
	Player getPlayer(String name);
}
