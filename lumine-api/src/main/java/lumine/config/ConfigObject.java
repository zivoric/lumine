package lumine.config;

import lumine.config.type.TypeAny;
import lumine.config.type.TypeObject;

import java.util.*;

public class ConfigObject extends ConfigEntry<TypeObject> {
    private final Map<String, ConfigEntry<?>> defaults;
    private final Map<String, ConfigEntry<?>> properties;
    private final boolean storesDefaultProperties;

    public ConfigObject(Map<String, ConfigEntry<?>> defaults, Map<String, ConfigEntry<?>> properties) {
        super(null);
        this.storesDefaultProperties = false;
        Map<String, TypeAny> types = new HashMap<>();
        Map<String, ConfigEntry<?>> defConsumer = new HashMap<>(defaults);
        this.properties = new HashMap<>(properties);
        this.defaults = defaults;
            /* TODO: add functionality for placeholders representing other properties
                add a new Config class with constructor<T, R extends ConfigEntry<T>>(String name, T type, Function<ConfigEntry<?>, R> operator)
                where 'operator' is a function that converts the input property (of type 'name') into a value of the specified type;
                the specified property must be processed and removed from the entrySet (and defConsumer) beforehand
             */
        for (Map.Entry<String, ConfigEntry<?>> entry : this.properties.entrySet()) {
            String name = entry.getKey();
            ConfigEntry<?> property = entry.getValue();
            if (!validateEntry(name, property)) {
                types.put(name, property.type.setRemovable(true));
            } else {
                ConfigEntry<?> def = this.defaults.get(name);
                defConsumer.remove(name);
                properties.put(name, property.asType(def.type.setRemovable(!(def instanceof ConfigPlaceholder))));
                types.put(name, def.type);
            }
        }
        for (Map.Entry<String, ConfigEntry<?>> def : defConsumer.entrySet()) {
            if (def.getValue() instanceof ConfigPlaceholder) {
                throw new IllegalArgumentException("Property value '" + def.getKey() + "' must be provided");
            }
        }
        this.type = new TypeObject(types);
        /*
            for each default:
            - if default holds value and properties.get(name) == null, this.get(name) should return its default
            - if default isAssignableFrom property, and property holds value, ignore the default
            - if default is placeholder, property must be set, or throw error
            - for any of these cases, if property is a placeholder, throw error (placeholders are only used for marking required values without set defaults)
            - for any of these cases, defaults are always removable=false, so the value of default.removable can be ignored
            then, add any remaining properties as dynamic (removable, TypeAny) properties
            these dynamic properties should be the only removable=true properties, along with any that were added using methods (like put(name, value))
            --
            Remember: The defaults should be stored without modification (ever)!! Checking for default replacement is done within the get methods.
                      This also means that the properties are checked only for validity in constructor; logic with defaults is done within the get methods,
                      and so once the constructor is done, properties should not have been modified.
         */
    }
    public ConfigObject(Map<String, ConfigEntry<?>> properties) {
        this(false, properties);
    }
    protected ConfigObject(boolean storesDefaultProperties, Map<String, ConfigEntry<?>> properties) {
        super(null);
        this.storesDefaultProperties = storesDefaultProperties;
        this.properties = properties;
        this.defaults = new HashMap<>();
        Map<String, TypeAny> types = new HashMap<>();
        for (Map.Entry<String, ConfigEntry<?>> entry : properties.entrySet()) {
            types.put(entry.getKey(), entry.getValue().type.setRemovable(true));
        }
        this.type = new TypeObject(types);
    }

    public Map<String, ConfigEntry<?>> getDefaults() {
        return Collections.unmodifiableMap(defaults);
    }

    public Map<String, ConfigEntry<?>> asMap() {
        return Collections.unmodifiableMap(properties);
    }

    public Map<String, Object> asValueMap() {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<String, ConfigEntry<?>> entry : properties.entrySet()) {
            values.put(entry.getKey(), entry.getValue().asValue());
        }
        return values;
    }

    public int size() {
        return properties.size();
    }

    public boolean isDefault(String key) {
        return (properties.get(key) == null && defaults.containsKey(key));
    }

    public ConfigEntry<?> get(String key) {
        ConfigEntry<?> value = properties.get(key);
        if (value == null && defaults.containsKey(key)) {
            ConfigEntry<?> def = defaults.get(key);
            return def instanceof ConfigDynamic<?> dynamic ? dynamic.convert(get(key)) : def;
        }
        return value;
    }

    public ConfigEntry<?> put(String key, ConfigEntry<?> value) {
        if (!validateEntry(key, value)) {
            HashMap<String, TypeAny> newTypes = new HashMap<>(this.type.getTypes());
            newTypes.put(key, value.type.setRemovable(true));
            this.type = new TypeObject(newTypes);
            return properties.put(key, value);
        } else {
            ConfigEntry<?> def = this.defaults.get(key);
            HashMap<String, TypeAny> newTypes = new HashMap<>(this.type.getTypes());
            newTypes.put(key, def.type.setRemovable(!(def instanceof ConfigPlaceholder)));
            this.type = new TypeObject(newTypes);
            return properties.put(key, value.asType(def.type));
        }
    }

    public ConfigEntry<?> remove(String key) {
        ConfigEntry<?> property = properties.get(key);
        if (property == null) {
            return null;
        }
        if (property.type.isRemovable()) {
            HashMap<String, TypeAny> newTypes = new HashMap<>(this.type.getTypes());
            newTypes.remove(key);
            this.type = new TypeObject(newTypes);
            return properties.remove(key);
        }
        throw new IllegalArgumentException("Property '" + key + "' cannot be removed");
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(ConfigEntry<?> value) {
        return properties.containsValue(value);
    }

    private boolean validateEntry(String key, ConfigEntry<?> value) {
        if (!defaults.containsKey(key)) {
            // return value represents whether the value is a known value (unknown values are dynamically typed)
            return false;
        }
        ConfigEntry<?> def = defaults.get(key);
        if (!storesDefaultProperties && value instanceof ConfigPlaceholder) {
            throw new IllegalArgumentException("Provided property value '" + key + "' must not be a placeholder");
        }
        if (!storesDefaultProperties && value instanceof ConfigDynamic) {
            throw new IllegalArgumentException("Provided property value '" + key + "' must not be a dynamic");
        }
        if (!def.type.canRetrieveFrom(value.type)) {
            throw new IllegalArgumentException("Property '" + key + "' is of type " + def.type + "; " + value.type + " provided");
        }
        if (def instanceof ConfigPlaceholder && properties.get(key) == null) {
            throw new IllegalArgumentException("Property '" + key + "' must not be null (is required)");
        }
        return true;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    private String toString(int layer) {
        String tabs = "\t".repeat(layer);
        StringBuilder builder = new StringBuilder(tabs);
        builder.append("{\n");
        int i = 0;
        for (Map.Entry<String, ConfigEntry<?>> entry : properties.entrySet()) {
            String valueStr;
            if (entry.getValue() instanceof ConfigObject obj) {
                valueStr = obj.toString(i+1);
            } else {
                valueStr = entry.getValue().toString();
            }
            builder.append(tabs)
                    .append("\t\"")
                    .append(entry.getKey())
                    .append('\"')
                    .append(": ")
                    .append(valueStr);
            if (i > size() - 1) {
                builder.append(",\n"); // new line for every property, could also be made space
            }
            i++;
        }
        return builder.append(tabs).append("}").toString();
    }
}
