package conduit.entity;

public class Entity<T extends net.minecraft.entity.Entity> {
	private final T nEntity;
	public Entity(T nEntity) {
		this.nEntity = nEntity;
	}
	
	public T toMinecraft() {
		return nEntity;
	}
}
