package conduit.bridge.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import conduit.command.argument.Argument;
import net.minecraft.server.command.ServerCommandSource;

public abstract class SimpleArgumentBuilder<A extends Argument<T>, B extends ArgumentType<T>, T> extends ArgumentBuilder<A, B, T, T>  {
    public SimpleArgumentBuilder(A argument) {
        super(argument);
    }
    public SimpleArgumentBuilder() {
        super();
    }

    @Override
    public T convertValue(T value, CommandContext<ServerCommandSource> source) {
        return value;
    }
}
