package conduit.entity.player;

import conduit.command.CommandSender;
import conduit.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

public class Player extends Entity<PlayerEntity> implements CommandSender {
	public Player(PlayerEntity entity) {
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
