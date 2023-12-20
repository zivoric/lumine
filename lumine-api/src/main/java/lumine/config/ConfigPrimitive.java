package lumine.config;

import lumine.config.type.TypeAny;

import java.util.Objects;

public abstract class ConfigPrimitive<T extends TypeAny,V> extends ConfigEntry<T> {
    protected V value;
    protected ConfigPrimitive(T type, V value) {
        super(type);
        setValue(value);
    }
    public final V getValue() {
        return value;
    }
    public final V setValue(V value) {
        V oldValue = this.value;
        this.value = Objects.requireNonNull(value);
        return oldValue;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
