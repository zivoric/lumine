package lumine.launch.client;

import lumine.Lumine;
import lumine.launch.LumineTweaker;
import lumine.prisma.launch.Prisma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LumineClient {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = LumineTweaker.class.getName();
        Lumine.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Lumine.addOrReplaceArgs(list, "--lumineEnvironment", "CLIENT");
        Prisma.main(list.toArray(new String[0]));
    }
}
