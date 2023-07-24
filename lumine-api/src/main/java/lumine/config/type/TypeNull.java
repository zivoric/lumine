package lumine.config.type;

public class TypeNull extends TypeAny {
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        return other instanceof TypeNull;
    }
}
