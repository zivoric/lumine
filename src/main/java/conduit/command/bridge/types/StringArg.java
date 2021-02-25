package conduit.command.bridge.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;

import conduit.command.bridge.Argument;

public class StringArg extends Argument<String> {
	private final String identifier;
	private final StringArgumentType arg;
	private final StrType type;
	private StringArg(String identifier, StringArgumentType type) {
		arg = type;
		this.identifier = identifier;
		this.type = StrType.fromBrigadierType(type.getType());
	}
	private StringArg(String identifier, StrType type) {
		switch (type) {
		case GREEDY:
			arg = StringArgumentType.greedyString();
			break;
		case QUOTABLE:
			arg = StringArgumentType.string();
			break;
		case SINGLE:
			arg = StringArgumentType.word();
			break;
		default:
			arg = null;
			break;
		}
		this.identifier = identifier;
		this.type = type;
	}
	private StringArg(String identifier, StringType type) {
		switch (type) {
		case GREEDY_PHRASE:
			arg = StringArgumentType.greedyString();
			break;
		case QUOTABLE_PHRASE:
			arg = StringArgumentType.string();
			break;
		case SINGLE_WORD:
			arg = StringArgumentType.word();
			break;
		default:
			arg = null;
			break;
		}
		this.identifier = identifier;
		this.type = StrType.fromBrigadierType(type);
		
	}
	public static StringArg stringArg(String identifier, StringType type) {
		return new StringArg(identifier, type);
	}
	public static StringArg stringArg(String identifier, StrType type) {
		return new StringArg(identifier, type);
	}
	public static StringArg stringArg(String identifier, StringArgumentType type) {
		return new StringArg(identifier, type);
	}
	@Override
	public StringArgumentType toBrigadier() {
		return arg;
	}
	public StrType getStringType() {
		return type;
	}
	public enum StrType {
		SINGLE(StringType.SINGLE_WORD), QUOTABLE(StringType.QUOTABLE_PHRASE), GREEDY(StringType.GREEDY_PHRASE);
		private final StringType type;
		StrType(StringType type) {
			this.type = type;
		}
		public StringType getBrigadierType() {
			return type;
		}
		public static StrType fromBrigadierType(StringType type) {
			switch (type) {
			case GREEDY_PHRASE:
				return GREEDY;
			case QUOTABLE_PHRASE:
				return QUOTABLE;
			case SINGLE_WORD:
				return SINGLE;
			default:
				return null;
			}
		}
	}
	@Override
	public Class<String> getType() {
		return String.class;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	public static StringArg fromBrigadier(String identifier, ArgumentType<String> type) {
		return new StringArg(identifier, (StringArgumentType) type);
	}
}
