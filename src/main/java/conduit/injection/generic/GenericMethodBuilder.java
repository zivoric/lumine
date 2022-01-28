package conduit.injection.generic;

public final class GenericMethodBuilder<T> {
    public GenericMethodBuilder(Class<T> cl) {
        this.cl = cl;
    }

    private final Class<T> cl;
    private Integer index = null;
    private String name = null;

    public GenericMethodBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }
    public GenericMethodBuilder<T> withIndex(int index) {
        this.index = index;
        return this;
    }
    public GenericMethod<T> build() {
        return new GenericMethod<>(cl, index, name);
    }
}
