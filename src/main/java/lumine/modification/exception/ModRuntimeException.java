package lumine.modification.exception;

public class ModRuntimeException extends ModException {
    public ModRuntimeException(String message) {
        super(message, ModRuntimeException.class);
    }
    private ModRuntimeException(Exception e) {
        super(e);
    }
    public static ModRuntimeException create(Exception e) {
        if (e instanceof ModRuntimeException mle) {
            return mle;
        } else {
            return new ModRuntimeException(e);
        }
    }
}
