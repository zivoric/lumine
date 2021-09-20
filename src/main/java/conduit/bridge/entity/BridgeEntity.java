package conduit.bridge.entity;

import conduit.bridge.server.BridgeServer;
import conduit.entity.Entity;
import conduit.server.Server;
import net.minecraft.text.LiteralText;

public class BridgeEntity<T extends net.minecraft.entity.Entity> extends Entity {
	private final T nEntity;
	public BridgeEntity(T nEntity) {
		this.nEntity = nEntity;
	}
	
	public T toMinecraft() {
		return nEntity;
	}

	public Server getServer() {
		return new BridgeServer(nEntity.getServer());
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
