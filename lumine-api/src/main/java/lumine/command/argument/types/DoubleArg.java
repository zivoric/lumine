package lumine.command.argument.types;

import lumine.command.argument.Argument;

public class DoubleArg extends Argument<Double> {
	private final double min;
	private final double max;
	private final String identifier;
	private DoubleArg(String identifier) {
		this(identifier, Double.MIN_VALUE, Double.MAX_VALUE);
	}
	private DoubleArg(String identifier, double min) {
		this(identifier, min, Double.MAX_VALUE);
	}
	private DoubleArg(String identifier, double min, double max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
	}

	public double min() {
		return min;
	}
	public double max() {
		return max;
	}

	public static DoubleArg doubleArg(String identifier) {
		return new DoubleArg(identifier);
	}
	public static DoubleArg doubleArg(String identifier, double min) {
		return new DoubleArg(identifier, min);
	}
	public static DoubleArg doubleArg(String identifier, double min, double max) {
		return new DoubleArg(identifier, min, max);
	}
	@Override
	public Class<Double> getType() {
		return Double.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}
