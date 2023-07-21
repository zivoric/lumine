package lumine.command.argument.types;

import lumine.command.argument.Argument;

public class StringArg extends Argument<String> {
	private final String identifier;
	private final StrType type;
	private StringArg(String identifier, StrType type) {
		this.identifier = identifier;
		this.type = type;
	}
	public StrType type() {
		return type;
	}
	public static StringArg stringArg(String identifier, StrType type) {
		return new StringArg(identifier, type);
	}
	public StrType getStringType() {
		return type;
	}
	public enum StrType {
		SINGLE, QUOTABLE, GREEDY
	}
	@Override
	public Class<String> getType() {
		return String.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
}
