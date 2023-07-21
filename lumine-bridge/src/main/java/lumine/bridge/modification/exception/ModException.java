package lumine.bridge.modification.exception;

public class ModException extends Exception {
    private final Class<? extends Exception> eClass;
    public ModException(String message) {
        this(message, ModException.class);
    }
    protected ModException(String message, Class<? extends Exception> eClass) {
        super(message);
        this.eClass = eClass;
    }
    protected ModException(Exception e) {
        this(e.getClass().getName() + ": " + e.getMessage(), e.getClass());
        setStackTrace(e.getStackTrace());
    }

    public static ModException create(Exception e) {
        if (e instanceof ModException mle) {
            return mle;
        } else {
            return new ModException(e);
        }
    }
    public Class<? extends Exception> getExceptionClass() {
        return eClass;
    }
}
