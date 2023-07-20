package lumine.bridge.command.builders;

import com.mojang.brigadier.arguments.LongArgumentType;
import lumine.bridge.command.SimpleArgumentBuilder;
import lumine.command.argument.types.LongArg;

public class LongArgBuilder extends SimpleArgumentBuilder<LongArg, LongArgumentType, Long> {
    public LongArgBuilder(LongArg argument) {
        super(argument);
    }

    @Override
    public Class<Long> getBrigadierClass() {
        return Long.class;
    }

    public LongArgBuilder() {
        super();
    }

    @Override
    public LongArgumentType toBrigadier() {
        return LongArgumentType.longArg(getArgument().min(), getArgument().max());
    }

    @Override
    public LongArg fromBrigadier(String identifier, LongArgumentType argument) {
        return LongArg.longArg(identifier, argument.getMinimum(), argument.getMaximum());
    }
}
