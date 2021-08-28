package conduit.command;

import java.util.Arrays;
import java.util.List;

import conduit.bridge.command.Argument;
import conduit.bridge.command.Command;
import conduit.bridge.command.CommandInformation;
import conduit.chat.ChatUtils;
import conduit.main.ConduitConstants;
import conduit.util.IDKey;

public class VersionCommand extends Command {
	@Override
	public String getLiteralName() {
		return "version";
	}

	@Override
	public int onCommand(CommandInformation info) {
		CommandSender sender = info.getSender();
		sender.sendMessage(
				ChatUtils.CHAT_PREFIX + "Running conduit version " + ChatUtils.highlightWord(ConduitConstants.instance().CONDUIT_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(ConduitConstants.instance().MINECRAFT_VERSION_NAME) + ".");
			return 0;
	}

	@Override
	public List<Argument<?>> getArgs() {
		return Arrays.asList(new Argument<?>[0]);
	}
	/*public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(CommandManager.literal("version").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(
				ChatUtils.CHAT_PREFIX + "Running conduit version " + ChatUtils.highlightWord(ConduitConstants.CONDUIT_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(ConduitConstants.MINECRAFT_VERSION) + "."), false);
			return 0;
		}));
	}*/

	@Override
	public IDKey getIdentifier() {
		return IDKey.conduit("version");
	}
}