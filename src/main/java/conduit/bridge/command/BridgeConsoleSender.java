package conduit.bridge.command;

import conduit.chat.ChatColors;
import conduit.command.ConsoleSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class BridgeConsoleSender implements ConsoleSender {
	private final CommandOutput output;
	private final ServerCommandSource source;
	public BridgeConsoleSender(MinecraftServer server) {
		source = server.getCommandSource();
		output = server;
	}
	@Override
	public void sendMessage(String message) {
		source.sendFeedback(new LiteralText(ChatColors.removeColorCodes(message)), false);
	}

	@Override
	public void sendError(String message) {
		source.sendError(new LiteralText(ChatColors.removeColorCodes(message)));
	}

}