package conduit.bridge.entity;

import conduit.entity.Entity;

public class BridgeEntity<T extends net.minecraft.entity.Entity> implements Entity {
	private final T nEntity;
	public BridgeEntity(T nEntity) {
		this.nEntity = nEntity;
	}
	
	public T toMinecraft() {
		return nEntity;
	}
}
