package lumine.config;

import lumine.config.type.TypeAny;
import lumine.config.type.TypeObject;

import java.util.HashMap;
import java.util.Map;

public class ConfigObject extends ConfigEntry<TypeObject> {
    private final Map<String, ConfigEntry<?>> properties;
    private final Map<String, PropertyInfo<?>> info;

    public ConfigObject(Map<String, PropertyInfo<?>> defaults, Map<String, ConfigEntry<?>> properties) {
        super(null);
        /* TODO
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
        super(null);
        this.info = new HashMap<>();
        this.type = dynamicType(properties, info);
        this.properties = new HashMap<>(properties);
        //fillProperties(this.types, this.properties);
    }

    /* TODO
        remove() - remove key from mappings entirely, change type (throws error if removable is false)
        unset() - set map values to placeholder, do not change type (removable must be true)
     */

    private static TypeObject dynamicType(Map<String, ConfigEntry<?>> properties, Map<String, PropertyInfo<?>> infoMap) {
        Map<String, TypeAny> types = new HashMap<>();
        for (Map.Entry<String, ConfigEntry<?>> entry : properties.entrySet()) {
            String name = entry.getKey();
            ConfigEntry<?> value = entry.getValue();
            PropertyInfo<?> info = new PropertyInfo(value.type, value, true);
            infoMap.put(name, info);
            types.put(name, value.type);
        }
        return new TypeObject(types);
    }

    private static void fillProperties(Map<String, TypeAny> types, Map<String, ConfigEntry<?>> properties) {
        for (Map.Entry<String, TypeAny> typeEntry : types.entrySet()) {
            String name = typeEntry.getKey();
            TypeAny type = typeEntry.getValue();
            if (!properties.containsKey(name)) {
                throw new IllegalArgumentException("Object does not have ")
            } else if (!)
        }
    }

    public record PropertyInfo<T extends TypeAny>(T type, ConfigEntry<T> value, boolean removable) {
        public PropertyInfo(T type, ConfigEntry<T> value) {
            this(type, value, true);
        }
    }
}
