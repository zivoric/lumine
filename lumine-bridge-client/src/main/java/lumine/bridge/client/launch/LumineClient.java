package lumine.bridge.client.launch;

import lumine.Lumine;
import lumine.bridge.client.ClientInjects;
import lumine.bridge.client.launch.LumineClientTweaker;
import lumine.prisma.launch.Prisma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LumineClient {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = LumineClientTweaker.class.getName();
        Lumine.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Lumine.addOrReplaceArgs(list, "--lumineEnvironment", "CLIENT");
        Lumine.addOrReplaceArgs(list, "--obfuscation", "INTERMEDIARY");
        Prisma.main(list.toArray(new String[0]));
    }
}
