package conduit.modification;

public abstract class ChildMod {
    private boolean enabled = false;

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
    public boolean isEnabled() {
        return enabled;
    }

    final void initialize() {
        if (enabled) throw new IllegalStateException("Mod already initialized");
        start();
        enabled = true;
    }
    final void terminate() throws IllegalStateException {
        if (!enabled) throw new IllegalStateException("Mod is not initialized");
        stop();
        enabled = false;
    }
    public abstract void start();
    public abstract void stop();
}
