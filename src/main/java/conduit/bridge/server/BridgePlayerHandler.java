package conduit.bridge.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import conduit.bridge.entity.BridgePlayer;
import conduit.entity.Player;
import conduit.server.PlayerHandler;
import conduit.server.Server;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;

public class BridgePlayerHandler implements PlayerHandler {
	private final PlayerManager manager;
	public BridgePlayerHandler(PlayerManager minecraft) {
		manager = minecraft;
	}
	
	@Override
	public Server getServer() {
		return new BridgeServer(manager.getServer());
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		manager.getPlayerList().forEach(player -> {
			players.add(new BridgePlayer(player));
		});
		return players;
	}

	@Override
	public Player getPlayer(UUID uuid) {
		PlayerEntity pl = manager.getPlayer(uuid);
		return pl == null ? null : new BridgePlayer(pl);
	}

	@Override
	public Player getPlayer(String name) {
		PlayerEntity pl = manager.getPlayer(name);
		return pl == null ? null : new BridgePlayer(pl);
	}

}
