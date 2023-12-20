package lumine.config.type;

import java.util.Collections;
import java.util.Map;

public class TypeObject extends TypeAny {

    private final Map<String, TypeAny> types;
    public TypeObject(Map<String, TypeAny> types) {
        this.types = types;
    }
    
    @Override
    public boolean canRetrieveFrom(TypeAny other) {
        // keys must be the same in order for the default checking to work
        if (other instanceof TypeObject otherObj) {
            for (Map.Entry<String, TypeAny> entry : types.entrySet()) {
                // if any type in this is not a supertype of the corresponding type in other, return false
                TypeAny type = entry.getValue();
                TypeAny otherType = otherObj.getTypes().get(entry.getKey());
                if (!type.canRetrieveFrom(otherType) || (!type.isRemovable() && otherType.isRemovable())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Map<String, TypeAny> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString() + "{");
        int i = 0;
        for (Map.Entry<String, TypeAny> entry : types.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().toString());
            if (!entry.getValue().isRemovable()) {
                builder.append("!");
            }
            if (i > types.size() - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.append("}").toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TypeObject otherObj && types.keySet().equals(otherObj.types.keySet())) {
            for (Map.Entry<String, TypeAny> entry : types.entrySet()) {
                // if any type in this is not the same as the corresponding type in other, return false
                if (!entry.getValue().equals(otherObj.getTypes().get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
