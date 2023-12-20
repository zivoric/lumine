package lumine.config.type;

public class TypeString extends TypePrimitive {
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return !(other == null || other instanceof TypeObject || other instanceof TypeArray);
    }
}
