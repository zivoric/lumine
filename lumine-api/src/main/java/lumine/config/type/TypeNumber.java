package lumine.config.type;

public class TypeNumber extends TypePrimitive {
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypeNumber;
    }
}
