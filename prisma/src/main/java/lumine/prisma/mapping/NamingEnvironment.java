package lumine.prisma.mapping;

public enum NamingEnvironment {
    OFFICIAL( false), OFFICIAL_NAMED(false),
    YARN_INTERMEDIARY(true), YARN_NAMED(true);

    private final boolean requiresYarn;
    NamingEnvironment(boolean requiresYarn) {
        this.requiresYarn = requiresYarn;
    }

    public boolean requiresYarn() {
        return requiresYarn;
    }
}
