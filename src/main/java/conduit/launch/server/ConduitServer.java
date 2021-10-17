package conduit.launch.server;

import conduit.launch.ConduitTweaker;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConduitServer {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = ConduitTweaker.class.getName();
        addOrReplace(list, "--tweakClass", tweakClass);
        addOrReplace(list, "--conduitEnvironment", "SERVER");
        Launch.main(list.toArray(new String[0]));
    }
    private static void addOrReplace(List<String> list, String prefix, String arg) {
        if (list.contains(prefix)) {
            list.add(list.indexOf(prefix)+1, arg);
        } else {
            list.add(prefix);
            list.add(arg);
        }
    }
}