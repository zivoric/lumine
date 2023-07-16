package conduit;

import conduit.util.GameEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Conduit {
    private static GameEnvironment environment = null;
    public static final Logger LOGGER = LogManager.getLogger("Conduit");

    public static void setEnvironment(GameEnvironment env) throws IllegalAccessException {
        if (environment == null)
            environment = env;
        else
            throw new IllegalAccessException("Game environment is already set");
    }
    public static GameEnvironment getEnvironment() {
        return environment;
    }

    public static boolean isClient() {
        return getEnvironment() == GameEnvironment.CLIENT;
    }

    public static void log(String text) {
        LOGGER.info("[Conduit] " + text);
    }
    public static void log(String... text) {
        for (String t : text) {
            log(t);
        }
    }
    public static void warn(String text) {
        LOGGER.warn("[Conduit] " + text);
    }
    public static void warn(String... text) {
        for (String t : text) {
            warn(t);
        }
    }
    public static void error(String text) {
        LOGGER.error("[Conduit] " + text);
    }
    public static void error(String... text) {
        for (String t : text) {
            error(t);
        }
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
