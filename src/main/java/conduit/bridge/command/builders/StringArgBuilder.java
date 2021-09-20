package conduit.bridge.command.builders;

import com.mojang.brigadier.arguments.StringArgumentType;
import conduit.bridge.command.SimpleArgumentBuilder;
import conduit.command.argument.types.StringArg;

public class StringArgBuilder extends SimpleArgumentBuilder<StringArg, StringArgumentType, String> {
    public StringArgBuilder(StringArg argument) {
        super(argument);
    }

    @Override
    public Class<String> getBrigadierClass() {
        return String.class;
    }

    public StringArgBuilder() {
        super();
    }

    @Override
    public StringArgumentType toBrigadier() {
        StringArgumentType arg;
        switch (getArgument().type()) {
            case GREEDY -> arg = StringArgumentType.greedyString();
            case QUOTABLE -> arg = StringArgumentType.string();
            case SINGLE -> arg = StringArgumentType.word();
            default -> arg = null;
        }
        return arg;
    }

    @Override
    public StringArg fromBrigadier(String identifier, StringArgumentType argument) {
        return StringArg.stringArg(identifier, fromBrigadierType(argument.getType()));
    }

    public static StringArg.StrType fromBrigadierType(StringArgumentType.StringType type) {
        return switch (type) {
            case GREEDY_PHRASE -> StringArg.StrType.GREEDY;
            case QUOTABLE_PHRASE -> StringArg.StrType.QUOTABLE;
            case SINGLE_WORD -> StringArg.StrType.SINGLE;
        };
    }
}
