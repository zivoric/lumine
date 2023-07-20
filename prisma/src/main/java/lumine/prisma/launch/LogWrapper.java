package lumine.prisma.launch;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogWrapper {
    private static final String PREFIX = "[Prisma] ";

    private static LogWrapper wrapper = null;

    private final Logger logger;

    private LogWrapper() {
        logger = LogManager.getFormatterLogger("Prisma");
    }

    static LogWrapper create() {
        if (wrapper == null) {
            wrapper = new LogWrapper();
        }
        return instance();
    }

    public static LogWrapper instance() {
        return wrapper;
    }

    public void log(Level level, String message, Object... args) {
        logger.log(level, PREFIX + message, args);
    }

    public void log(Level level, String message, Throwable e) {
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

    public void trace(String message, Object... args) {
        log(Level.TRACE, message, args);
    }

    public void trace(String message, Throwable e) {
        log(Level.TRACE, message, e);
    }

    public void error(String message, Object... args) {
        log(Level.ERROR, message, args);
    }

    public void error(String message, Throwable e) {
        log(Level.ERROR, message, e);
    }
}
