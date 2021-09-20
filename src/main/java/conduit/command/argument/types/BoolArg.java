package conduit.command.argument.types;

import conduit.command.argument.Argument;

public class BoolArg extends Argument<Boolean> {
	private final String identifier;
	private BoolArg(String identifier) {
		this.identifier = identifier;
	}
	public static BoolArg bool(String identifier) {
		return new BoolArg(identifier);
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
