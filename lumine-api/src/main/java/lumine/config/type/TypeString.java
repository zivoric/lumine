package lumine.config.type;

public class TypeString extends TypePrimitive {
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other != null;
    }
}
