package conduit.launch.client;

import conduit.Conduit;
import conduit.launch.ConduitTweaker;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConduitClient {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        String tweakClass = ConduitTweaker.class.getName();
        Conduit.addOrReplaceArgs(list, "--tweakClass", tweakClass);
        Conduit.addOrReplaceArgs(list, "--conduitEnvironment", "CLIENT");
        Launch.main(list.toArray(new String[0]));
    }
}
