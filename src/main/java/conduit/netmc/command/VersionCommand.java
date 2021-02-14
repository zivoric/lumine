package conduit.netmc.command;

import com.mojang.brigadier.CommandDispatcher;

import conduit.chat.ChatUtils;
import conduit.main.ConduitConstants;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class VersionCommand extends Command {
	public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(CommandManager.literal("version").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(
				ChatUtils.CHAT_PREFIX + "Running conduit version " + ChatUtils.highlightWord(ConduitConstants.CONDUIT_VERSION)
				+ " on minecraft version " + ChatUtils.highlightWord(ConduitConstants.MINECRAFT_VERSION) + "."), false);
			return 0;
		}));
	}
}