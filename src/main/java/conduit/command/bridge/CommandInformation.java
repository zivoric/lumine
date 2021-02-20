package conduit.command.bridge;

import conduit.command.Command;
import conduit.command.CommandSender;
import conduit.command.ConsoleSender;
import conduit.entity.player.Player;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

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

public static CommandSender getSender(ServerCommandSource source) {
	if (source.getEntity() == null) {
		return new ConsoleSender(source.getMinecraftServer());
	} else if (source.getEntity() instanceof PlayerEntity) {
		return new Player((PlayerEntity) source.getEntity());
	} else {
		return null;
	}
}
}
