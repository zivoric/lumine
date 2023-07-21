package lumine.bridge.entity;

import lumine.bridge.server.BridgeServer;
import lumine.entity.Entity;
import lumine.server.Server;
import lumine.util.location.BlockLocation;
import lumine.util.location.DoubleLocation;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.UUID;

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
		toMinecraft().getCommandSource().sendFeedback(MutableText.of(new LiteralTextContent(message)), false);
	}

	@Override
	public void sendError(String message) {
		toMinecraft().getCommandSource().sendError(MutableText.of(new LiteralTextContent(message)));
	}

	@Override
	public UUID getUUID() {
		return toMinecraft().getUuid();
	}

	@Override
	public void teleport(double x, double y, double z) {
		toMinecraft().teleport(x, y, z);
	}

	@Override
	public void teleport(DoubleLocation location) {
		teleport(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public void teleport(Entity e) {
		teleport(e.getLocation());
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof Entity e) && getUUID().equals(e.getUUID());
	}
}
