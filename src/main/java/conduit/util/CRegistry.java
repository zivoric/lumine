package conduit.util;

import conduit.command.Command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CRegistry<T> implements Iterable<Entry<IDKey, T>> {
	public final static CRegistry<Command> COMMANDS = new CRegistry<>();
	
	private final Map<IDKey, T> registry;
	
	private CRegistry() {
		this(new HashMap<>());
	}
	
	private CRegistry(Map<IDKey, T> registry) {
		this.registry = registry;
	}
	
	public boolean isRegistered(IDKey key) {
		return registry.containsKey(key);
	}
	
	public boolean isRegistered(T value) {
		return registry.containsValue(value);
	}
	
	public boolean isTypeRegistered(Class<?> cl) {
		for (T value : registry.values()) {
			if (cl.isInstance(value))
				return true;
		}
		return false;
	}
	
	public T get(IDKey key) {
		return registry.get(key);
	}
	
	public boolean add(IDKey key, T value) {
		if (isRegistered(key))
			return false;
		registry.put(key, value);
		return true;
	}
	
	public boolean addAll(Map<IDKey, T> all) {
		boolean changed = false;
		for (Entry<IDKey, T> entry : all.entrySet()) {
			if (add(entry.getKey(), entry.getValue())) {
				changed = true;
			}
		}
		return changed;
	}
	
	/*public boolean remove(IDKey key) {
		for (CEntry<T> entry : registry) {
			if (entry.getKey().equals(key)) {
				registry.remove(entry);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeValue(IDKey key, T value) {
		for (CEntry<T> entry : registry) {
			if (entry.getKey().equals(key) && entry.getValue().equals(value)) {
				registry.remove(entry);
				return true;
			}
		}
		return false;
	}*/
	
	public void clear() {
		registry.clear();
	}

	@Override
	public Iterator<Entry<IDKey, T>> iterator() {
		return registry.entrySet().iterator();
	}
}