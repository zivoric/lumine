package conduit.command.bridge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class IntegerArg extends Argument<Integer> {
	private final IntegerArgumentType arg;
	private final int min;
	private final int max;
	private final String identifier;
	private IntegerArg(String identifier, IntegerArgumentType type) {
		this.identifier = identifier;
		min = type.getMinimum();
		max = type.getMaximum();
		arg = type;
	}
	private IntegerArg(String identifier) {
		this.identifier = identifier;
		min = Integer.MIN_VALUE;
		max = Integer.MAX_VALUE;
		arg = IntegerArgumentType.integer(min, max);
	}
	private IntegerArg(String identifier, int min) {
		this.identifier = identifier;
		this.min = min;
		this.max = Integer.MAX_VALUE;
		arg = IntegerArgumentType.integer(min, max);
	}
	private IntegerArg(String identifier, int min, int max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
		arg = IntegerArgumentType.integer(min, max);
	}
	public static IntegerArg integer(String identifier) {
		return new IntegerArg(identifier);
	}
	public static IntegerArg integer(String identifier, int min) {
		return new IntegerArg(identifier, min);
	}
	public static IntegerArg integer(String identifier, int min, int max) {
		return new IntegerArg(identifier, min, max);
	}
	@Override
	public IntegerArgumentType toBrigadier() {
		return arg;
	}
	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
	public static IntegerArg fromBrigadier(String identifier, ArgumentType<Integer> type) {
		return new IntegerArg(identifier, (IntegerArgumentType) type);
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}