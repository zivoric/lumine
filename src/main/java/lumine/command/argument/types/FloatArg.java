package lumine.command.argument.types;

import lumine.command.argument.Argument;

public class FloatArg extends Argument<Float> {
	private final float min;
	private final float max;
	private final String identifier;
	private FloatArg(String identifier) {
		this(identifier, Float.MIN_VALUE, Float.MAX_VALUE);
	}
	private FloatArg(String identifier, float min) {
		this(identifier, min, Float.MAX_VALUE);
	}
	private FloatArg(String identifier, float min, float max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
	}
	public float min() {
		return min;
	}
	public float max() {
		return max;
	}
	public static FloatArg floatArg(String identifier) {
		return new FloatArg(identifier);
	}
	public static FloatArg floatArg(String identifier, float min) {
		return new FloatArg(identifier, min);
	}
	public static FloatArg floatArg(String identifier, float min, float max) {
		return new FloatArg(identifier, min, max);
	}
	@Override
	public Class<Float> getType() {
		return Float.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}