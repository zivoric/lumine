package conduit.launch.server;

import conduit.Conduit;
import conduit.launch.ConduitTweaker;
import conduit.prisma.launch.Prisma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConduitServer {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = ConduitTweaker.class.getName();
        Conduit.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Conduit.addOrReplaceArgs(list, "--conduitEnvironment", "SERVER");
        Prisma.main(list.toArray(new String[0]));
    }
}
