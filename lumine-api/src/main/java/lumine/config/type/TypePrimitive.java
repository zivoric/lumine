package lumine.config.type;

public class TypePrimitive extends TypeAny {
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypePrimitive;
    }
}
