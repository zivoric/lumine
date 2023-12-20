package lumine.config.type;

public class TypeArray<T extends TypeAny> extends TypeAny {
    private final T type;
    public TypeArray(T type) {
        this.type = type;
    }

    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypeArray<?> arr && getComponentType().canRetrieveFrom(arr.getComponentType());
    }

    public T getComponentType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + type + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeArray<?> arr && getComponentType().equals(arr.getComponentType());
    }
}
