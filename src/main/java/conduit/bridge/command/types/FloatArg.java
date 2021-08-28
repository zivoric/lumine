package conduit.bridge.command.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;

import conduit.bridge.command.Argument;

public class FloatArg extends Argument<Float> {
	private final FloatArgumentType arg;
	private final float min;
	private final float max;
	private final String identifier;
	private FloatArg(String identifier, FloatArgumentType type) {
		this.identifier = identifier;
		min = type.getMinimum();
		max = type.getMaximum();
		arg = type;
	}
	private FloatArg(String identifier) {
		this.identifier = identifier;
		min = Float.MIN_VALUE;
		max = Float.MAX_VALUE;
		arg = FloatArgumentType.floatArg(min, max);
	}
	private FloatArg(String identifier, float min) {
		this.identifier = identifier;
		this.min = min;
		this.max = Float.MAX_VALUE;
		arg = FloatArgumentType.floatArg(min, max);
	}
	private FloatArg(String identifier, float min, float max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
		arg = FloatArgumentType.floatArg(min, max);
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
	public FloatArgumentType toBrigadier() {
		return arg;
	}
	@Override
	public Class<Float> getType() {
		return Float.class;
	}
	public static FloatArg fromBrigadier(String identifier, ArgumentType<Float> type) {
		return new FloatArg(identifier, (FloatArgumentType) type);
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}