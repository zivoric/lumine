package lumine.bridge.client.launch;

import lumine.bridge.launch.LumineTweaker;
import lumine.prisma.launch.LaunchClassLoader;

public class LumineClientTweaker extends LumineTweaker {
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectWithDefinitions(classLoader, "");
    }
}
