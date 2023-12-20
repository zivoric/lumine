package lumine.config.type;

public class TypeBoolean extends TypePrimitive {

    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypeBoolean;
    }
}
