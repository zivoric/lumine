package lumine.config;

import lumine.config.type.TypeNumber;

public class ConfigBoolean extends ConfigPrimitive<TypeNumber, Boolean> {
    public ConfigBoolean(Boolean value) {
        super(new TypeNumber(), value);
    }
}
