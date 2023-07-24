package lumine.prisma.launch;

import lumine.prisma.utils.LogWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LaunchLogWrapper implements LogWrapper {
    private static final String PREFIX = "[Prisma] ";

    private static LaunchLogWrapper wrapper = null;

    private final Logger logger;

    private LaunchLogWrapper() {
        logger = LogManager.getFormatterLogger("Prisma");
    }

    static LaunchLogWrapper create() {
        if (wrapper == null) {
            wrapper = new LaunchLogWrapper();
        }
        return instance();
    }

    public static LaunchLogWrapper instance() {
        return wrapper;
    }

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
