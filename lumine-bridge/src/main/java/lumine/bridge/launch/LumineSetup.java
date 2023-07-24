package lumine.bridge.launch;

import lumine.Lumine;
import lumine.util.GameProfile;
import lumine.modification.ModManagerCore;
import lumine.prisma.utils.LogWrapper;
import lumine.prisma.utils.GameEnvironment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

class LumineSetup {
    public void setup(List<String> args, File game, String version) {
        Lumine.create();
        GameEnvironment environment;
        if (!args.contains("--lumineEnvironment") || args.lastIndexOf("--lumineEnvironment") == args.size()-1) {
            try {
                Class.forName("net.minecraft.client.main.Main", false, Thread.currentThread().getContextClassLoader());
                environment = GameEnvironment.CLIENT;
            } catch (ClassNotFoundException e) {
                environment = GameEnvironment.SERVER;
            }
            Lumine.getLogger().warn("Environment not specified, set to inferred environment " + environment);
        } else {
            String envStr = args.get(args.indexOf("--lumineEnvironment") + 1).toUpperCase();
            try {
                environment = GameEnvironment.valueOf(envStr);
            } catch (IllegalArgumentException e) {
                try {
                    Class.forName("net.minecraft.client.main.Main", false, Thread.currentThread().getContextClassLoader());
                    environment = GameEnvironment.CLIENT;
                } catch (ClassNotFoundException cnf) {
                    environment = GameEnvironment.SERVER;
                }
                Lumine.getLogger().warn("Environment '%s' not valid, set to inferred environment " + environment, envStr);
            }
            try {
                GameEnvironment.setEnvironment(environment);
            } catch (IllegalStateException e) {
                throw new IllegalArgumentException("Environment already set at launch. This should never happen!");
            }
            try {
                Lumine.getLumine().setLogger(new LumineSetup.Log4JLogWrapper());
            } catch (IllegalStateException e) {
                throw new IllegalArgumentException("Main logger already set at launch. This should never happen!");
            }
        }

        ModManagerCore.initialize();
        ModManagerCore loader = ModManagerCore.getInstance();
        loader.prepareMods();
        loader.initializeMods();

        GameProfile.instance(version.substring(0, version.indexOf("-lumine")), game);
        Lumine.getLogger().info("Lumine tweaker class loaded! Running minecraft " + GameEnvironment.getEnvironment().toString().toLowerCase() + " version " + GameProfile.instance().MINECRAFT_VERSION_NAME);
        Lumine.getLogger().info("Game directory: " + game.toURI());
    }

    private static class Log4JLogWrapper implements LogWrapper {
        private static final String PREFIX = "[Lumine] ";
        private final Logger logger = LogManager.getFormatterLogger("Lumine");

        private void log(Level level, String message, Object... args) {
            logger.log(level, PREFIX + message, args);
        }

        private void log(Level level, String message, Throwable e) {
            logger.log(level, PREFIX + message, e);
        }

        public void info(String message, Object... args) {
            this.log(Level.INFO, message, args);
        }

        public void warn(String message, Object... args) {
            log(Level.WARN, message, args);
        }

        public void warn(String message, Throwable e) {
            log(Level.WARN, message, e);
        }

        public void debug(String message, Object... args) {
            log(Level.DEBUG, message, args);
        }

        public void debug(String message, Throwable e) {
            log(Level.DEBUG, message, e);
        }

        public void error(String message, Object... args) {
            log(Level.ERROR, message, args);
        }

        public void error(String message, Throwable e) {
            log(Level.ERROR, message, e);
        }
    }
}
