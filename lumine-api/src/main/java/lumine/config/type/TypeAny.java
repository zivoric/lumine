package lumine.config.type;

public class TypeAny {

    /**
     * Determines whether the given type {@code other} can be converted to this type.
     * This is different from {@link #isAssignableFrom} in that {@link TypePlaceholder}
     * values cannot be retrieved as any other type.
     * @see #isAssignableFrom(TypeAny)
     * @param other the other type to compare to
     * @return {@code true} if the given type can be retrieved from this type; {@code false} otherwise
     */
    public boolean canRetrieveFrom(TypeAny other) {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeAny type && this.getClass().isAssignableFrom(type.getClass());
    }
}
