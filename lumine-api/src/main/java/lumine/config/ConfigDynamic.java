package lumine.config;

import lumine.config.type.TypeAny;

import java.util.function.Function;

public class ConfigDynamic<T extends TypeAny> extends ConfigEntry<T> {
    private final String name;
    private final Function<ConfigEntry<?>, ? extends ConfigEntry<T>> operator;
    public ConfigDynamic(String name, T type, Function<ConfigEntry<?>, ? extends ConfigEntry<T>> operator) {
        super(type);
        this.name = name;
        this.operator = operator;
    }

    public String inputName() {
        return this.name;
    }

    public ConfigEntry<T> convert(ConfigEntry<?> input) {
        return operator.apply(input);
    }

    @Override
    public String toString() {
        return "Dynamic(" + type + ")";
    }
}
