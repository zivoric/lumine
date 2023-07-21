package lumine;

import lumine.prisma.LogWrapper;

import java.util.List;

public class Lumine {
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


    public static void addOrReplaceArgs(List<String> list, String prefix, String arg) {
        if (list.contains(prefix)) {
            list.add(list.indexOf(prefix)+1, arg);
        } else {
            list.add(prefix);
            list.add(arg);
        }
    }
}
