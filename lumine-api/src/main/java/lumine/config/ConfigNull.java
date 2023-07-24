package lumine.config;

import lumine.config.type.TypeNull;
import lumine.config.type.TypeNumber;

public class ConfigNull extends ConfigPrimitive<TypeNull, Object> {
    public ConfigNull() {
        super(new TypeNull(), null);
    }
}
