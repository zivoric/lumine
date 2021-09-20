package conduit.command;

import conduit.command.argument.Argument;
import conduit.command.argument.CommandInformation;
import conduit.util.IDKey;

import java.util.List;

public abstract class Command {
	public abstract String getLiteralName();
	public abstract IDKey getIdentifier();
	public abstract int onCommand(CommandInformation info);
	public abstract List<Argument<?>> getArgs();
}
