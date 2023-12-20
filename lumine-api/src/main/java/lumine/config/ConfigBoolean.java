package lumine.config;

import lumine.config.type.TypeBoolean;

public class ConfigBoolean extends ConfigPrimitive<TypeBoolean, Boolean> {
    public ConfigBoolean(Boolean value) {
        super(new TypeBoolean(), value);
    }
}
