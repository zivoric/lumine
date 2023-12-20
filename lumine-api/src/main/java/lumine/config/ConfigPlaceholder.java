package lumine.config;

import lumine.config.type.TypeAny;
import lumine.config.type.TypeString;

public class ConfigPlaceholder<T extends TypeAny> extends ConfigEntry<T> {
    public ConfigPlaceholder(T type) {
        super(type);
    }

    @Override
    public String asString() {
        throw invalid();
    }
    @Override
    public Number asNumber() {
        throw invalid();
    }
    @Override
    public Boolean asBoolean() {
        throw invalid();
    }
    @Override
    public ConfigObject asObject() {
        throw invalid();
    }
    @Override
    public <U extends TypeAny> ConfigArray<U, ConfigEntry<U>> asArray(Class<? extends U> typeClass) {
        throw invalid();
    }
    @Override
    public ConfigArray<?, ?> asArray() {
        throw invalid();
    }
    private UnsupportedOperationException invalid() {
        return new UnsupportedOperationException("Cannot get placeholder as a valued entry");
    }

    @Override
    public String toString() {
        return "Placeholder(" + type + ")";
    }
}
