package lumine.bridge.launch.server;

import lumine.Lumine;
import lumine.bridge.injects.CoreInjects;
import lumine.bridge.launch.LumineTweaker;
import lumine.prisma.launch.Prisma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LumineServer {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = LumineTweaker.class.getName();
        LumineTweaker.addInjectors(new CoreInjects().getInjectors());
        Lumine.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Lumine.addOrReplaceArgs(list, "--lumineEnvironment", "SERVER");
        Prisma.main(list.toArray(new String[0]));
    }
}
