package conduit.command.bridge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public abstract class Argument<T> {
	public abstract String getIdentifier();
	public abstract Class<T> getType();
	public abstract ArgumentType<T> toBrigadier();
	@SuppressWarnings("unchecked")
	public static <T> Argument<T> getFromBrigadier(String identifier, ArgumentType<T> type) {
		if (type instanceof DoubleArgumentType) {
			return (Argument<T>) DoubleArg.fromBrigadier(identifier, (ArgumentType<Double>) type);
		} else if (type instanceof BoolArgumentType) {
			return (Argument<T>) BoolArg.fromBrigadier(identifier, (ArgumentType<Boolean>) type);
		} else if (type instanceof FloatArgumentType) {
			return (Argument<T>) FloatArg.fromBrigadier(identifier, (ArgumentType<Float>) type);
		} else if (type instanceof IntegerArgumentType) {
			return (Argument<T>) IntegerArg.fromBrigadier(identifier, (ArgumentType<Integer>) type);
		} else if (type instanceof LongArgumentType) {
			return (Argument<T>) LongArg.fromBrigadier(identifier, (ArgumentType<Long>) type);
		} else if (type instanceof StringArgumentType) {
			return (Argument<T>) StringArg.fromBrigadier(identifier, (ArgumentType<String>) type);
		} else {
			return null;
		}
	}
}
