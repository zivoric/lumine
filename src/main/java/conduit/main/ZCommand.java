package conduit.main;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ZCommand {
	private static int egg = 0;
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(CommandManager.literal("zcomm").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText("Test number: " + egg + "."), false);
			return 0;
		}).then(CommandManager.argument("value", IntegerArgumentType.integer(0, 255)).executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText("Test number set to " + IntegerArgumentType.getInteger(ex, "value")), false);
			egg = IntegerArgumentType.getInteger(ex, "value");
			return 0;
		})));
	}
}
