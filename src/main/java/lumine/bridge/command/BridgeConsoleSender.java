package lumine.bridge.command;

import lumine.bridge.server.BridgeServer;
import lumine.chat.ChatColors;
import lumine.command.ConsoleSender;
import lumine.server.Server;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;

public class BridgeConsoleSender implements ConsoleSender {
	private final MinecraftServer output;
	private final ServerCommandSource source;
	public BridgeConsoleSender(MinecraftServer server) {
		source = server.getCommandSource();
		output = server;
	}
	@Override
	public void sendMessage(String message) {
		source.sendFeedback(MutableText.of(new LiteralTextContent(ChatColors.removeColorCodes(message))), false);
	}

	@Override
	public void sendError(String message) {
		source.sendError(MutableText.of(new LiteralTextContent(ChatColors.removeColorCodes(message))));
	}

	@Override
	public Server getServer() {
		return new BridgeServer(output);
	}
}
