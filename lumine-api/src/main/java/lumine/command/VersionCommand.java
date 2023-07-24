package lumine.command;

import lumine.util.GameProfile;
import lumine.chat.ChatUtils;
import lumine.command.argument.Argument;
import lumine.command.argument.CommandInformation;
import lumine.util.IDKey;

import java.util.Arrays;
import java.util.List;

public class VersionCommand extends Command {
	@Override
	public String getLiteralName() {
		return "version";
	}

	@Override
	public int onCommand(CommandInformation info) {
		CommandSender sender = info.getSender();
		sender.sendMessage(
				ChatUtils.CHAT_PREFIX + "Running lumine version " + ChatUtils.highlightWord(GameProfile.instance().LUMINE_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(GameProfile.instance().MINECRAFT_VERSION_NAME) + ".");
			return 0;
	}

	@Override
	public List<Argument<?>> getArgs() {
		return Arrays.asList(new Argument<?>[0]);
	}
	/*public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(CommandManager.literal("version").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(
				ChatUtils.CHAT_PREFIX + "Running lumine version " + ChatUtils.highlightWord(LumineConstants.LUMINE_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(LumineConstants.MINECRAFT_VERSION) + "."), false);
			return 0;
		}));
	}*/

	@Override
	public IDKey getIdentifier() {
		return IDKey.lumine("version");
	}
}