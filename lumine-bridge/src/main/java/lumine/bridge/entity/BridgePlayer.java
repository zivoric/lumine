package lumine.bridge.entity;

import lumine.bridge.server.BridgePlayerProfile;
import lumine.entity.Player;
import lumine.server.PlayerProfile;
import lumine.world.GameMode;
import net.minecraft.server.network.ServerPlayerEntity;

public class BridgePlayer extends BridgeEntity<ServerPlayerEntity> implements Player {
	private final PlayerProfile profile;

	public BridgePlayer(ServerPlayerEntity entity) {
		super(entity);
		profile = new BridgePlayerProfile(entity.getGameProfile());
	}

	@Override
	public PlayerProfile getProfile() {
		return profile;
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.valueOf(toMinecraft().interactionManager.getGameMode().toString());
	}

	@Override
	public boolean setGameMode(GameMode arg) {
		return toMinecraft().changeGameMode(net.minecraft.world.GameMode.byName(arg.toString().toLowerCase()));
	}

	@Override
	public String getName() {
		return toMinecraft().getName().getString();
	}
}
