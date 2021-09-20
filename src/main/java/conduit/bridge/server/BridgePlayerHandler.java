package conduit.bridge.server;

import com.mojang.authlib.GameProfile;
import conduit.bridge.entity.BridgePlayer;
import conduit.entity.Player;
import conduit.server.PlayerHandler;
import conduit.server.PlayerProfile;
import conduit.server.Server;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;

import java.util.*;

public class BridgePlayerHandler implements PlayerHandler {
	private final PlayerManager manager;

	private final UserCache userCache;
	private final int maxPlayers;

	public BridgePlayerHandler(PlayerManager minecraft) {
		manager = minecraft;
		maxPlayers = manager.getMaxPlayerCount();
		userCache = manager.getServer().getUserCache();
	}
	
	@Override
	public Server getServer() {
		return new BridgeServer(manager.getServer());
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		manager.getPlayerList().forEach(player -> players.add(new BridgePlayer(player)));
		return Collections.unmodifiableList(players);
	}

	@Override
	public Player getPlayer(UUID uuid) {
		ServerPlayerEntity pl = manager.getPlayer(uuid);
		return pl == null ? null : new BridgePlayer(pl);
	}

	@Override
	public Player getPlayer(String name) {
		ServerPlayerEntity pl = manager.getPlayer(name);
		return pl == null ? null : new BridgePlayer(pl);
	}

	@Override
	public int getCurrentPlayers() {
		return manager.getCurrentPlayerCount();
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public List<PlayerProfile> getOperators() {
		return Arrays.stream(manager.getOpList().getNames()).map(op -> {
			Optional<GameProfile> profile = userCache.findByName(op);
			return profile.map(pr->(PlayerProfile) new BridgePlayerProfile(pr)).orElse(null);
		}).filter(Objects::nonNull).toList();
	}

	@Override
	public List<Player> getOnlineOperators() {
		return manager.getPlayerList().stream().filter(pl->manager.getOpList().isOp(pl.getGameProfile())).map(pl-> (Player) new BridgePlayer(pl)).toList();
	}

	@Override
	public boolean isOperator(Player player) {
		if (player instanceof BridgePlayer bridge) {
			return manager.getOpList().isOp(bridge.toMinecraft().getGameProfile());
		} else {
			return false;
		}
	}

}
