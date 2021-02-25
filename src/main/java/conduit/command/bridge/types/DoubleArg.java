package conduit.command.bridge.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import conduit.command.bridge.Argument;

public class DoubleArg extends Argument<Double> {
	private final DoubleArgumentType arg;
	private final double min;
	private final double max;
	private final String identifier;
	private DoubleArg(String identifier, DoubleArgumentType type) {
		this.identifier = identifier;
		min = type.getMinimum();
		max = type.getMaximum();
		arg = type;
	}
	private DoubleArg(String identifier) {
		this.identifier = identifier;
		min = Double.MIN_VALUE;
		max = Double.MAX_VALUE;
		arg = DoubleArgumentType.doubleArg(min, max);
	}
	private DoubleArg(String identifier, double min) {
		this.identifier = identifier;
		this.min = min;
		this.max = Double.MAX_VALUE;
		arg = DoubleArgumentType.doubleArg(min, max);
	}
	private DoubleArg(String identifier, double min, double max) {
		this.identifier = identifier;
		this.min = min;
		this.max = max;
		arg = DoubleArgumentType.doubleArg(min, max);
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
	public DoubleArgumentType toBrigadier() {
		return arg;
	}
	@Override
	public Class<Double> getType() {
		return Double.class;
	}
	public static DoubleArg fromBrigadier(String identifier, ArgumentType<Double> type) {
		return new DoubleArg(identifier, (DoubleArgumentType) type);
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}
