package conduit.bridge.command.builders;

import com.mojang.brigadier.arguments.BoolArgumentType;
import conduit.bridge.command.SimpleArgumentBuilder;
import conduit.command.argument.types.BoolArg;

public class BoolArgBuilder extends SimpleArgumentBuilder<BoolArg, BoolArgumentType, Boolean> {
    public BoolArgBuilder(BoolArg argument) {
        super(argument);
    }

    @Override
    public Class<Boolean> getBrigadierClass() {
        return Boolean.class;
    }

    public BoolArgBuilder() {
        super();
    }

    @Override
    public BoolArgumentType toBrigadier() {
        return BoolArgumentType.bool();
    }

    @Override
    public BoolArg fromBrigadier(String identifier, BoolArgumentType argument) {
        return BoolArg.bool(identifier);
    }
}
