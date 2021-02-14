package conduit.netmc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import conduit.chat.ChatUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class TestCommand extends Command {
	private static int egg = 0;
	public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(CommandManager.literal("zcomm").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(ChatUtils.CHAT_PREFIX + "Test number: " + ChatUtils.highlightWord(""+egg) + "."), false);
			return 0;
		}).then(CommandManager.argument("value", IntegerArgumentType.integer(0, 255)).executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(ChatUtils.CHAT_PREFIX + "Test number set to " + ChatUtils.highlightWord(""+IntegerArgumentType.getInteger(ex, "value"))), false);
			egg = IntegerArgumentType.getInteger(ex, "value");
			return 0;
		})));
	}
}
