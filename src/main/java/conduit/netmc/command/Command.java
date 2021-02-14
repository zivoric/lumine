package conduit.netmc.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.ServerCommandSource;

public abstract class Command {
	public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
	}
}
