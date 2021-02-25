package conduit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import conduit.command.Command;
import conduit.main.Conduit;

public class CRegistry<T> implements Iterable<CEntry<T>> {
	public final static CRegistry<Command> COMMANDS = new CRegistry<Command>();
	
	private final List<CEntry<T>> registry;
	
	private CRegistry() {
		registry = new ArrayList<CEntry<T>>();
	}
	
	private CRegistry(Map<IDKey, T> registry) {
		List<CEntry<T>> entries = new ArrayList<CEntry<T>>();
		registry.entrySet().forEach(entry -> entries.add(new CEntry<T>(entry.getKey(), entry.getValue())));
		this.registry = entries;
	}
	
	public boolean isRegistered(IDKey key) {
		for (CEntry<T> entry : registry) {
			if (entry.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRegistered(T value) {
		for (CEntry<T> entry : registry) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isTypeRegistered(Class<?> cl) {
		for (CEntry<T> entry : registry) {
			if (cl.isInstance(entry.getValue()))
				return true;
		}
		return false;
	}
	
	public T get(IDKey key) {
		for (CEntry<T> entry : registry) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public boolean add(IDKey key, T value) {
		if (isRegistered(key)) 
			remove(key);
		if (get(key) != null && get(key).equals(value))
			return false;
		registry.add(new CEntry<T>(key, value));
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
	
	public boolean remove(IDKey key) {
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
	}
	
	public void clear() {
		registry.clear();
	}

	@Override
	public Iterator<CEntry<T>> iterator() {
		return registry.iterator();
	}
}