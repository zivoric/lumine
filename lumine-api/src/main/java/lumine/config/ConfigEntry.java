package lumine.config;

import lumine.config.type.*;

import javax.lang.model.type.ArrayType;

public abstract class ConfigEntry<T extends TypeAny> {
    protected T type;
    protected ConfigEntry(T type) {
        this.type = type;
    }
    public final boolean isNull() {
        return type() instanceof TypeNull;
    }
    public final boolean isObject() {
        return type() instanceof TypeObject;
    }
    public final boolean isArray() {
        return type() instanceof TypeArray;
    }
    public final boolean isString() {
        return type() instanceof TypeString;
    }
    public final boolean isBoolean() {
        return type() instanceof TypeBoolean;
    }
    public final boolean isNumber() {
        return type() instanceof TypeNumber;
    }
    public String asString() {
        return this.toString();
    }
    public Number asNumber() {
        return asNumberWrapped().value;
    }
    public Boolean asBoolean() {
        return asBooleanWrapped().value;
    }
    public ConfigObject asObject() {
        if (isObject())
            return (ConfigObject) this;
        throw invalid("Object");
    }
    public <U extends TypeAny> ConfigArray<U,ConfigEntry<U>> asArray(Class<? extends U> typeClass) {
        if (isArray())
            return (ConfigArray<U,ConfigEntry<U>>) this;
        throw invalid("Array");
    }

    public ConfigArray<?,?> asArray() {
        if (isArray())
            return (ConfigArray<?, ?>) this;
        throw invalid("Array");
    }
    public ConfigString asStringWrapped() {
        if (isString())
            return (ConfigString) this;
        else
            return new ConfigString(asString());
    }
    public ConfigNumber asNumberWrapped() {
        if (isNumber())
            return (ConfigNumber) this;
        throw invalid("Number");
    }
    public ConfigBoolean asBooleanWrapped() {
        if (isBoolean())
            return (ConfigBoolean) this;
        throw invalid("Boolean");
    }

    private UnsupportedOperationException invalid(String type) {
        return new UnsupportedOperationException("Entry of type " + type() + " cannot be retrieved as " + type);
    }

    public final T type() {
        return type;
    }

    public ConfigEntry<?> asType(TypeAny type) {

    }
}
