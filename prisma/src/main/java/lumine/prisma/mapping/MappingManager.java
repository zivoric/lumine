package lumine.prisma.mapping;

import lumine.prisma.refract.MethodInfo;

import java.io.File;

public interface MappingManager {
    boolean remap(NamingEnvironment from, NamingEnvironment to, File input, File output);

    String remapClassName(NamingEnvironment from, NamingEnvironment to, String inputName);
    String remapMethodName(NamingEnvironment from, NamingEnvironment to, MethodInfo inputMethod);
    MethodInfo remapMethodInfo(NamingEnvironment from, NamingEnvironment to, MethodInfo inputMethod);
}
