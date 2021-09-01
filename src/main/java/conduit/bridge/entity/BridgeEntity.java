package conduit.bridge.entity;

import conduit.bridge.server.BridgeServer;
import conduit.entity.Entity;
import conduit.server.Server;

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
}
