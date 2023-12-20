package lumine.config.type;

public class TypeAny {
    protected Boolean removable = null;
    public TypeAny() {
    }

    public boolean canRetrieveFrom(TypeAny other) {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().concat(isRemovable() ? "" : "!");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeAny type && this.getClass().isAssignableFrom(type.getClass());
    }

    public TypeAny setRemovable(boolean removable) {
        if (this.removable != null)
            throw new UnsupportedOperationException("This type is already removable="+ removable + ", cannot be reassigned");
        this.removable = removable;
        return this;
    }

    public boolean isRemovable() {
        // assume removable=false (stricter case)
        return removable != null && removable;
    }
}
