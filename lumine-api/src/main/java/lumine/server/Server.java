package lumine.server;

import lumine.entity.Player;

import java.util.UUID;

public interface Server {
	PlayerHandler getPlayerHandler();
	default Player getPlayer(String name) {
		return getPlayerHandler().getPlayer(name);
	}
	default Player getPlayer(UUID uuid) {
		return getPlayerHandler().getPlayer(uuid);
	}
}
