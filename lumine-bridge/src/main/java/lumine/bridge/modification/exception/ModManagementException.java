package lumine.bridge.modification.exception;

public class ModManagementException extends ModException {
    public ModManagementException(String message) {
        super(message, ModManagementException.class);
    }
    private ModManagementException(Exception e) {
        super(e);
    }
    public static ModManagementException create(Exception e) {
        if (e instanceof ModManagementException mle) {
            return mle;
        } else {
            return new ModManagementException(e);
        }
    }
}
