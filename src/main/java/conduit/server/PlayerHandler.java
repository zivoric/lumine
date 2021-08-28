package conduit.server;

import java.util.List;
import java.util.UUID;

import conduit.entity.Player;

public interface PlayerHandler {
	Server getServer();
	List<Player> getPlayers();
	Player getPlayer(UUID uuid);
	Player getPlayer(String name);
}
