package conduit.modification;

public abstract class ChildMod {
    private ConduitMod parent = null;
    public void setParent(ConduitMod parent) throws UnsupportedOperationException, IllegalArgumentException {
        if (this.parent == null && parent != null)  {
            this.parent = parent;
        } else {
            throw this.parent != null ? new UnsupportedOperationException("Parent is already set") : new IllegalArgumentException("Parent cannot be null");
        }
    }
    public ConduitMod getParent() {
        return parent;
    }
    public abstract void start();
    public abstract void stop();
}
