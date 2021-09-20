package conduit.command.argument.types;

import conduit.command.argument.Argument;
import conduit.entity.Entity;

import java.util.List;

public class EntityArg extends Argument<List<? extends Entity>> {
    private final String identifier;
    private final boolean onlyPlayers, allowMultiple;
    private EntityArg(String identifier, boolean allowMultiple, boolean onlyPlayers) {
        this.identifier = identifier;
        this.onlyPlayers = onlyPlayers;
        this.allowMultiple = allowMultiple;
    }
    public boolean allowMultiple() {
        return allowMultiple;
    }
    public boolean onlyPlayers() {
        return onlyPlayers;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<? extends Entity>> getType() {
        return (Class<List<? extends Entity>>)(Class<?>)List.class;
    }

    public static EntityArg entity(String identifier) {
        return new EntityArg(identifier, false, false);
    }

    public static EntityArg player(String identifier) {
        return new EntityArg(identifier, false, true);
    }

    public static EntityArg entities(String identifier) {
        return new EntityArg(identifier, true, false);
    }

    public static EntityArg players(String identifier) {
        return new EntityArg(identifier, true, true);
    }
}
