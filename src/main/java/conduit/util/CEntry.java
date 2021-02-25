package conduit.util;

public class CEntry<T> {
private final IDKey key;
private final T val;
CEntry(IDKey key, T val) {
	this.key = key;
	this.val = val;
}
public IDKey getKey() {
	return key;
}
public T getValue() {
	return val;
}

}
