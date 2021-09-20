package conduit.bridge.command.builders;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import conduit.bridge.command.SimpleArgumentBuilder;
import conduit.command.argument.types.DoubleArg;

public class DoubleArgBuilder extends SimpleArgumentBuilder<DoubleArg, DoubleArgumentType, Double> {
    public DoubleArgBuilder(DoubleArg argument) {
        super(argument);
    }

    @Override
    public Class<Double> getBrigadierClass() {
        return Double.class;
    }

    public DoubleArgBuilder() {
        super();
    }

    @Override
    public DoubleArgumentType toBrigadier() {
        return DoubleArgumentType.doubleArg(getArgument().min(), getArgument().max());
    }

    @Override
    public DoubleArg fromBrigadier(String identifier, DoubleArgumentType argument) {
        return DoubleArg.doubleArg(identifier, argument.getMinimum(), argument.getMaximum());
    }
}
