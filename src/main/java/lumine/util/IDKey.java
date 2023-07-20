package lumine.util;

import net.minecraft.util.Identifier;

public class IDKey {
private final String domain;
private final String key;
public static final String KEY_ALLOWED = "[a-z0-9-_/.]{1,100}";
public static final String DOMAIN_ALLOWED= "[a-z0-9-_.]{1,100}";
//private static final String KEY_DISALLOWED = "[^a-z0-9-_.]";
//private static final String DOMAIN_DISALLOWED = "[^a-z0-9-_.]";

private IDKey(String domain, String key) {
	this.domain = domain;
	this.key = key;
}

public static IDKey lumine(String key) {
	return custom("lumine", key);
}

public static IDKey minecraft(String key) {
	return custom("minecraft", key);
}

public static IDKey custom(String id) {
	String[] split = id.split(":");
	if (split.length == 1)
		return lumine(id);
	else
		return custom(split[0], split[1]);
}

public static IDKey custom(String domain, String key) throws IllegalArgumentException {
	if (!domain.matches(DOMAIN_ALLOWED))
		throw new IllegalArgumentException("Bad domain for ID");
	if (!key.matches(KEY_ALLOWED))
		throw new IllegalArgumentException("Bad key for ID");
	return new IDKey(domain, key);
}

public static IDKey fromMinecraft(Identifier id) {
	return custom(id.getNamespace(), id.getPath());
}

public Identifier toMinecraft() {
	return new Identifier(domain, key);
}

@Override
public boolean equals(Object key) {
	if (!(key instanceof IDKey idKey))
		return false;
	return idKey.key.equals(this.key) && idKey.domain.equals(this.domain);
}

@Override
public String toString() {
	return this.domain + ":" + this.key;
}
}
