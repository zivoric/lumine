package lumine;

import lumine.prisma.LogWrapper;
import lumine.util.GameEnvironment;

import java.util.List;

public class Lumine {
    private static GameEnvironment environment = null;
    private static LogWrapper mainLogger;

    public static void setLogger(LogWrapper logger) throws IllegalStateException {
        if (mainLogger == null)
            mainLogger = logger;
        else
            throw new IllegalStateException("Main logger is already set");
    }

    public static LogWrapper getLogger() {
        return mainLogger;
    }

    public static void setEnvironment(GameEnvironment env) throws IllegalStateException {
        if (environment == null)
            environment = env;
        else
            throw new IllegalStateException("Game environment is already set");
    }
    public static GameEnvironment getEnvironment() {
        return environment;
    }

    public static boolean isClient() {
        return getEnvironment() == GameEnvironment.CLIENT;
    }


    public static void addOrReplaceArgs(List<String> list, String prefix, String arg) {
        if (list.contains(prefix)) {
            list.add(list.indexOf(prefix)+1, arg);
        } else {
            list.add(prefix);
            list.add(arg);
        }
    }
}
