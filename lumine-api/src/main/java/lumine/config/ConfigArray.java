package lumine.config;

import lumine.config.type.TypeAny;
import lumine.config.type.TypeArray;
import lumine.config.type.TypePrimitive;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

public class ConfigArray<T extends TypeAny, U extends ConfigEntry<T>> extends ConfigEntry<TypeArray<T>> {
    private final ArrayList<U> entries;
    public ConfigArray(T type, U... entries) {
        this(type, List.of(entries));
    }
    public ConfigArray(T type, List<U> entries) {
        super(new TypeArray<>(type));
        this.entries = new ArrayList<>(entries);
    }

    /**
     * Returns an unmodifiable list of {@link ConfigEntry} entries representing this object.
     * @return an unmodifiable copy of the list of entries in this {@link ConfigArray}
     */
    public List<U> entries() {
        return Collections.unmodifiableList(entries);
    }

    public Object[] asPrimitiveArray() {
        return asPrimitiveArray(new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public <V> V[] asPrimitiveArray(V[] arrType) {
        Class<?> c = arrType.getClass().getComponentType();
        if (size() == 0) {
            return (V[]) Array.newInstance(c, 0);
        } else if (type.getComponentType() instanceof TypePrimitive) {
            V[] array = (V[]) Array.newInstance(c, size());
            Function<ConfigPrimitive<?,?>,?> func;
            if (c == Object.class) {
                func = ConfigPrimitive::getValue;
            }  else if (c == String.class) {
                func = ConfigEntry::asString;
            } else if (c == Number.class) {
                func = ConfigEntry::asNumber;
            } else if (c == Boolean.class) {
                func = ConfigEntry::asBoolean;
            } else if (c == Double.class) {
                func = p -> p.asNumber().doubleValue();
            } else if (c == Float.class) {
                func = p -> p.asNumber().floatValue();
            } else if (c == Long.class) {
                func = p -> p.asNumber().longValue();
            } else if (c == Integer.class) {
                func = p -> p.asNumber().intValue();
            } else if (c == Short.class) {
                func = p -> p.asNumber().shortValue();
            } else if (c == Byte.class) {
                func = p -> p.asNumber().byteValue();
            } else {
                func = (p) -> {
                    throw new IllegalArgumentException("Array type " + c.getName() + " is not a valid primitive type");
                };
            }
            ConfigPrimitive<?,?> primitive = (ConfigPrimitive<?,?>) this.get(0);
            try {
                for (int i = 0; i < array.length; i++) {
                    array[i] = (V) func.apply(primitive);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Array type mismatch", e);
            }
            return array;
        } else {
            throw new IllegalArgumentException("Array holds non-primitive type " + type.getComponentType());
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return entries.size();
    }

    public U get(int index) {
        return entries.get(index);
    }

    public U set(int index, U entry) {
        return entries.set(index, Objects.requireNonNull(entry));
    }

    public void add(int index, U entry) {
        entries.add(index, Objects.requireNonNull(entry));
    }

    public U remove(int index) {
        return entries.remove(index);
    }

    public void clear() {
        entries.clear();
    }

    public boolean addAll(Collection<? extends U> entries) {
        return addAll(size(), entries);
    }

    public boolean addAll(int index, Collection<? extends U> entries) {
        boolean changed = false;
        for (Iterator<? extends U> iterator = Objects.requireNonNull(entries).iterator(); iterator.hasNext(); changed = true) {
            add(index++, iterator.next());
        }
        return changed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        int i = 0;
        for (U u : entries) {
            builder.append(u.toString());
            if (i > size() - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.append("]").toString();
    }
}
