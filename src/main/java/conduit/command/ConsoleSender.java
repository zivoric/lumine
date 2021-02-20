package conduit.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ConsoleSender implements CommandSender {
	private final CommandOutput output;
	private final ServerCommandSource source;
	public ConsoleSender(MinecraftServer server) {
		source = server.getCommandSource();
		output = server;
	}
	@Override
	public void sendMessage(String message) {
		source.sendFeedback(new LiteralText(message), false);
	}

	@Override
	public void sendError(String message) {
		source.sendError(new LiteralText(message));
	}

}
