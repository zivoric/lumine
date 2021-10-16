package conduit.modification;

public abstract class ConduitMod {
    public abstract void initialize();
    public abstract void terminate();
    public Class<? extends ClientMod> getClientMod() {
        return null;
    }
    public Class<? extends ServerMod> getServerMod() {
        return null;
    }
}
