package lumine.prisma.refract.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;

public class RefractionUtils {
    public static final BiMap<Character, Class<?>> UNBOXED = HashBiMap.create(new HashMap<>() {{
        put('J', long.class);
        put('I', int.class);
        put('S', short.class);
        put('B', byte.class);
        put('C', char.class);
        put('F', float.class);
        put('D', double.class);
        put('Z', boolean.class);
        put('V', void.class);
    }});

    public static final BiMap<Character, Class<?>> BOXED = HashBiMap.create(new HashMap<>() {{
        put('J', Long.class);
        put('I', Integer.class);
        put('S', Short.class);
        put('B', Byte.class);
        put('C', Character.class);
        put('F', Float.class);
        put('D', Double.class);
        put('Z', Boolean.class);
        put('V', Void.class);
    }});

}
