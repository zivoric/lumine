package lumine.config;

import java.util.Map;

public class ConfigDefault extends ConfigObject {
    public ConfigDefault(Map<String, ConfigEntry<?>> properties) {
        super(true, properties);
    }

    @Override
    public Map<String, ConfigEntry<?>> getDefaults() {
        return this.asMap();
    }
}
