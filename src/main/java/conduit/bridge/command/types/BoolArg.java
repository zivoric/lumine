package conduit.bridge.command.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import conduit.bridge.command.Argument;

public class BoolArg extends Argument<Boolean> {
	private final BoolArgumentType arg;
	private final String identifier;
	private BoolArg(String identifier) {
		this.identifier = identifier;
		arg = BoolArgumentType.bool();
	}
	private BoolArg(String identifier, BoolArgumentType type) {
		this.identifier = identifier;
		arg = type;
	}
	public static BoolArg bool(String identifier) {
		return new BoolArg(identifier);
	}
	@Override
	public BoolArgumentType toBrigadier() {
		return arg;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	public static Argument<Boolean> fromBrigadier(String identifier, ArgumentType<Boolean> type) {
		return new BoolArg(identifier, (BoolArgumentType) type);
	}
	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
