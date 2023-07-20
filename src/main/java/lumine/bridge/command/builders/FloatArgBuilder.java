package lumine.bridge.command.builders;

import com.mojang.brigadier.arguments.FloatArgumentType;
import lumine.bridge.command.SimpleArgumentBuilder;
import lumine.command.argument.types.FloatArg;

public class FloatArgBuilder extends SimpleArgumentBuilder<FloatArg, FloatArgumentType, Float> {
    public FloatArgBuilder(FloatArg argument) {
        super(argument);
    }

    @Override
    public Class<Float> getBrigadierClass() {
        return Float.class;
    }

    public FloatArgBuilder() {
        super();
    }

    @Override
    public FloatArgumentType toBrigadier() {
        return FloatArgumentType.floatArg(getArgument().min(), getArgument().max());
    }

    @Override
    public FloatArg fromBrigadier(String identifier, FloatArgumentType argument) {
        return FloatArg.floatArg(identifier, argument.getMinimum(), argument.getMaximum());
    }
}
