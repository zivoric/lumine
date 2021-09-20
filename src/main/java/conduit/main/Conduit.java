package conduit.main;

import conduit.util.GameEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Conduit {
    public static final Logger LOGGER = LogManager.getLogger();
    public static GameEnvironment getEnvironment() {
        GameEnvironment env;
        try {
            Class.forName("net.minecraft.client.main.Main");
            env = GameEnvironment.CLIENT;
        } catch (ClassNotFoundException e) {
            env = GameEnvironment.SERVER;
        }
        return env;
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
}
