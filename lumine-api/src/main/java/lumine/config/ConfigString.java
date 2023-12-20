package lumine.config;

import lumine.config.type.TypeString;

public class ConfigString extends ConfigPrimitive<TypeString, String> {
    public ConfigString(String value) {
        super(new TypeString(), value);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
