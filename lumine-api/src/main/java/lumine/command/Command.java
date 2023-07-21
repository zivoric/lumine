package lumine.command;

import lumine.command.argument.Argument;
import lumine.command.argument.CommandInformation;
import lumine.util.IDKey;

import java.util.List;

public abstract class Command {
	public abstract String getLiteralName();
	public abstract IDKey getIdentifier();
	public abstract int onCommand(CommandInformation info);
	public abstract List<Argument<?>> getArgs();
}
