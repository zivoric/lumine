package lumine.bridge.modification.exception;

public class ModLoadException extends ModException {
    public ModLoadException(String message) {
        super(message, ModLoadException.class);
    }
    private ModLoadException(Exception e) {
        super(e);
    }
    public static ModLoadException create(Exception e) {
        if (e instanceof ModLoadException mle) {
            return mle;
        } else {
            return new ModLoadException(e);
        }
    }
}
