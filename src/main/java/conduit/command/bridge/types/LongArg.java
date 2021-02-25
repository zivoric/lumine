package conduit.command.bridge.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;

import conduit.command.bridge.Argument;

public class LongArg extends Argument<Long> {
	private final LongArgumentType arg;
	private final long min;
	private final long max;
	private final String identifier;
	private LongArg(String identifier, LongArgumentType type) {
		this.identifier = identifier;
		min = type.getMinimum();
		max = type.getMaximum();
		arg = type;
	}
	private LongArg(String identifier) {
		this.identifier = identifier;
		min = Long.MIN_VALUE;
		max = Long.MAX_VALUE;
		arg = LongArgumentType.longArg(min, max);
	}
	private LongArg(String identifier, long min) {
		this.identifier = identifier;
		this.min = min;
		this.max = Long.MAX_VALUE;
		arg = LongArgumentType.longArg(min, max);
	}
	private LongArg(String identifier, long min, long max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
		arg = LongArgumentType.longArg(min, max);
	}
	public static LongArg longArg(String identifier) {
		return new LongArg(identifier);
	}
	public static LongArg longArg(String identifier, long min) {
		return new LongArg(identifier, min);
	}
	public static LongArg longArg(String identifier, long min, long max) {
		return new LongArg(identifier, min, max);
	}
	@Override
	public LongArgumentType toBrigadier() {
		return arg;
	}
	@Override
	public Class<Long> getType() {
		return Long.class;
	}
	public static LongArg fromBrigadier(String identifier, ArgumentType<Long> type) {
		return new LongArg(identifier, (LongArgumentType) type);
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}
