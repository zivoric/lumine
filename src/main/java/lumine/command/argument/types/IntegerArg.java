package lumine.command.argument.types;

import lumine.command.argument.Argument;

public class IntegerArg extends Argument<Integer> {
	private final int min;
	private final int max;
	private final String identifier;
	private IntegerArg(String identifier) {
		this(identifier, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	private IntegerArg(String identifier, int min) {
		this(identifier, min, Integer.MAX_VALUE);
	}
	private IntegerArg(String identifier, int min, int max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
	}
	public int min() {
		return min;
	}
	public int max() {
		return max;
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
	public Class<Integer> getType() {
		return Integer.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}