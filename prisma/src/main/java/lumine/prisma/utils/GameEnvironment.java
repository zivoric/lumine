package lumine.prisma.utils;

public enum GameEnvironment {
    CLIENT, SERVER;

    private static GameEnvironment environment = null;

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
}
