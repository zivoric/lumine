package lumine.prisma.transform;

import lumine.prisma.launch.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultTweaker implements LaunchTweaker {
    private List<String> args = new ArrayList<>();
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
        this.args = args;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(DefaultTransformer.class.getName());
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return this.args.toArray(new String[0]);
    }
}
