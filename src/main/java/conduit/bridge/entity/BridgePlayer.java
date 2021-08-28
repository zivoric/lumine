package conduit.bridge.entity;

import conduit.entity.Player;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

public class BridgePlayer extends BridgeEntity<PlayerEntity> implements Player {
	public BridgePlayer(PlayerEntity entity) {
		super(entity);
	}
	
	@Override
	public void sendMessage(String message) {
		toMinecraft().getCommandSource().sendFeedback(new LiteralText(message), false);
	}

	@Override
	public void sendError(String message) {
		toMinecraft().getCommandSource().sendError(new LiteralText(message));
	}

}
