package conduit.command.argument.types;

import conduit.command.argument.Argument;

public class LongArg extends Argument<Long> {
	private final long min;
	private final long max;
	private final String identifier;
	private LongArg(String identifier) {
		this(identifier, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	private LongArg(String identifier, long min) {
		this(identifier, min, Long.MAX_VALUE);
	}
	private LongArg(String identifier, long min, long max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
	}
	public long min() {
		return min;
	}
	public long max() {
		return max;
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
	public Class<Long> getType() {
		return Long.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}
