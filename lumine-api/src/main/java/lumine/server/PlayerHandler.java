package lumine.server;

import lumine.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerHandler {
	Server getServer();
	List<Player> getPlayers();
	Player getPlayer(UUID uuid);
	Player getPlayer(String name);
	int getCurrentPlayers();
	int getMaxPlayers();
	List<PlayerProfile> getOperators();
	List<Player> getOnlineOperators();
	boolean isOperator(Player player);
}
