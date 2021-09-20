package conduit.command.argument;

import conduit.command.Command;
import conduit.command.CommandSender;

public class CommandInformation {
private final Command command;
private final ArgumentValue<?,?>[] args;
private final CommandSender sender;
public CommandInformation (Command command, CommandSender sender, ArgumentValue<?,?>... args) {
	this.sender = sender;
	this.command = command;
	this.args = args;
}
public Command getCommand() {
	return command;
}
public ArgumentValue<?,?>[] getArgs() {
	return args;
}
public CommandSender getSender() {
	return sender;
}

}
