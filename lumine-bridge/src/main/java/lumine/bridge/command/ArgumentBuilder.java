package lumine.bridge.command;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import lumine.bridge.command.builders.*;
import lumine.command.argument.Argument;
import lumine.command.argument.types.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public abstract class ArgumentBuilder<A extends Argument<T>, B extends ArgumentType<M>, T, M> {
    private final A argument;
    public ArgumentBuilder() {
        this(null);
    }
    public ArgumentBuilder(A argument) {
        this.argument = argument;
    }

    public abstract Class<M> getBrigadierClass();
    public A getArgument() {
        return argument;
    }
    public abstract B toBrigadier();
    public abstract A fromBrigadier(String identifier, B argument);
    public abstract T convertValue(M value, CommandContext<ServerCommandSource> context);

    @SuppressWarnings("unchecked")
    public static <A extends Argument<T>,B extends ArgumentType<M>,T,M> ArgumentBuilder<A,B,T,M> getBuilder(Argument<T> argument) {
        if (argument instanceof DoubleArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new DoubleArgBuilder(carg);
        } else if (argument instanceof BoolArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new BoolArgBuilder(carg);
        } else if (argument instanceof FloatArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new FloatArgBuilder(carg);
        } else if (argument instanceof IntegerArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new IntegerArgBuilder(carg);
        } else if (argument instanceof LongArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new LongArgBuilder(carg);
        } else if (argument instanceof StringArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new StringArgBuilder(carg);
        } else if (argument instanceof EntityArg carg) {
            return (ArgumentBuilder<A, B, T, M>) new EntityArgBuilder(carg);
        }  else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, M> Argument<T> getFromBrigadier(String identifier, ArgumentType<M> type) {
        if (type instanceof DoubleArgumentType ctype) {
            return (Argument<T>) new DoubleArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof BoolArgumentType ctype) {
            return (Argument<T>) new BoolArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof FloatArgumentType ctype) {
            return (Argument<T>) new FloatArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof IntegerArgumentType ctype) {
            return (Argument<T>) new IntegerArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof LongArgumentType ctype) {
            return (Argument<T>) new LongArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof StringArgumentType ctype) {
            return (Argument<T>) new StringArgBuilder().fromBrigadier(identifier, ctype);
        } else if (type instanceof EntityArgumentType ctype) {
            return (Argument<T>) new EntityArgBuilder().fromBrigadier(identifier, ctype);
        } else {
            return null;
        }
    }
}