package conduit.command;

import java.util.Arrays;
import java.util.List;

import conduit.chat.ChatUtils;
import conduit.command.bridge.Argument;
import conduit.command.bridge.CommandInformation;
import conduit.main.ConduitConstants;

public class VersionCommand extends Command {
	@Override
	public String getLiteralName() {
		return "version";
	}

	@Override
	public int onCommand(CommandInformation info) {
		CommandSender sender = info.getSender();
		sender.sendMessage(
				ChatUtils.CHAT_PREFIX + "Running conduit version " + ChatUtils.highlightWord(ConduitConstants.CONDUIT_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(ConduitConstants.MINECRAFT_VERSION) + ".");
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
}