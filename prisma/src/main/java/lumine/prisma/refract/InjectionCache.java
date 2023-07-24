package lumine.prisma.refract;

import java.util.HashMap;
import java.util.Map;

public class InjectionCache {
    private final Map<String, Object> CACHE = new HashMap<>();
    public void clear() {
        CACHE.clear();
    }
    public Object put(String m, Object o) {
        return CACHE.put(m, o);
    }
    public Object get(String m) {
        return CACHE.get(m);
    }
}
