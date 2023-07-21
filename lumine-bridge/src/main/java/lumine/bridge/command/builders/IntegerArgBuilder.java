package lumine.bridge.command.builders;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import lumine.bridge.command.SimpleArgumentBuilder;
import lumine.command.argument.types.IntegerArg;

public class IntegerArgBuilder extends SimpleArgumentBuilder<IntegerArg, IntegerArgumentType, Integer> {
    public IntegerArgBuilder(IntegerArg argument) {
        super(argument);
    }

    @Override
    public Class<Integer> getBrigadierClass() {
        return Integer.class;
    }

    public IntegerArgBuilder() {
        super();
    }

    @Override
    public IntegerArgumentType toBrigadier() {
        return IntegerArgumentType.integer(getArgument().min(), getArgument().max());
    }

    @Override
    public IntegerArg fromBrigadier(String identifier, IntegerArgumentType argument) {
        return IntegerArg.integer(identifier, argument.getMinimum(), argument.getMaximum());
    }
}
