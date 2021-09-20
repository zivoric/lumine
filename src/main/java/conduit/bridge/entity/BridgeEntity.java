package conduit.bridge.entity;

import conduit.bridge.server.BridgeServer;
import conduit.entity.Entity;
import conduit.server.Server;
import conduit.util.location.BlockLocation;
import conduit.util.location.DoubleLocation;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BridgeEntity<T extends net.minecraft.entity.Entity> implements Entity {
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
	public DoubleLocation getLocation() {
		Vec3d vec = nEntity.getPos();
		return new DoubleLocation(vec.x, vec.y, vec.z);
	}

	@Override
	public BlockLocation getBlockLocation() {
		Vec3i vec = nEntity.getBlockPos();
		return new BlockLocation(vec.getX(), vec.getY(), vec.getZ());
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
