package lumine.config;

import lumine.config.type.TypeNumber;

public class ConfigNumber extends ConfigPrimitive<TypeNumber, Number> {
    public ConfigNumber(Number value) {
        super(new TypeNumber(), value);
    }

}
