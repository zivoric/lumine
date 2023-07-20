package lumine.launch.server;

import lumine.Lumine;
import lumine.launch.LumineTweaker;
import lumine.prisma.launch.Prisma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LumineServer {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = LumineTweaker.class.getName();
        Lumine.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Lumine.addOrReplaceArgs(list, "--lumineEnvironment", "SERVER");
        Prisma.main(list.toArray(new String[0]));
    }
}
