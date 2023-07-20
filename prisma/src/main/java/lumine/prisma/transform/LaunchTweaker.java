package lumine.prisma.transform;

import lumine.prisma.launch.LaunchClassLoader;

import java.io.File;
import java.util.List;

public interface LaunchTweaker {

    void acceptOptions(List<String> args, File gameDir, final File assetsDir, String profile);

    void injectIntoClassLoader(LaunchClassLoader classLoader);

    String getLaunchTarget();

    String[] getLaunchArguments();

}
