package lumine.prisma.utils;

public interface LogWrapper {
    void info(String message, Object... args);

    void warn(String message, Object... args);

    void warn(String message, Throwable e);

    void debug(String message, Object... args);

    void debug(String message, Throwable e);
    void error(String message, Object... args);

    void error(String message, Throwable e);
}
