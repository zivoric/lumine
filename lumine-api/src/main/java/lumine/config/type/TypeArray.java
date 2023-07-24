package lumine.config.type;

public class TypeArray<T extends TypeAny> extends TypeAny {
    private final T type;
    public TypeArray(T type) {
        this.type = type;
    }
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypeArray<?> arr && getType().canRetrieveFrom(arr.getType());
    }

    public T getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + type + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeArray<?> arr && getType().equals(arr.getType());
    }
}
