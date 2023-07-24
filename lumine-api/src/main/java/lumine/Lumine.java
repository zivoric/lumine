package lumine;

import lumine.prisma.utils.LogWrapper;

import java.util.List;

public class Lumine {
    private static Lumine lumine = null;

    public static Lumine create() {
        if (lumine != null) {
            throw new IllegalStateException("Lumine instance cannot be created at game runtime");
        } else {
            lumine = new Lumine();
        }
        return lumine;
    }

    public static Lumine getLumine() {
        return lumine;
    }

    private LogWrapper mainLogger;

    public void setLogger(LogWrapper logger) throws IllegalStateException {
        if (mainLogger == null)
            mainLogger = logger;
        else
            throw new IllegalStateException("Main logger is already set");
    }

    public LogWrapper logger() {
        return mainLogger;
    }

    public static LogWrapper getLogger() {
        return getLumine().logger();
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
