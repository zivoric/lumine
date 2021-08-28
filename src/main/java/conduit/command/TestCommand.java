package conduit.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import conduit.bridge.command.Argument;
import conduit.bridge.command.ArgumentValue;
import conduit.bridge.command.Command;
import conduit.bridge.command.CommandInformation;
import conduit.bridge.command.types.IntegerArg;
import conduit.chat.ChatUtils;
import conduit.util.IDKey;

public class TestCommand extends Command {
	private static final List<Argument<?>> args = Arrays.asList(IntegerArg.integer("value", 0, 255));
	private static int egg = 0;
	/*public void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(getBuilder());
	}
	private LiteralArgumentBuilder<ServerCommandSource> getBuilder() {
		return CommandManager.literal("ctest").executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(ChatUtils.CHAT_PREFIX + "Test number: " + ChatUtils.highlightWord(""+egg) + "."), false);
			return 0;
		}).then(CommandManager.argument("value", IntegerArgumentType.integer(0, 255)).executes(ex -> {
			ex.getSource().sendFeedback(new LiteralText(ChatUtils.CHAT_PREFIX + "Test number set to " + ChatUtils.highlightWord(""+IntegerArgumentType.getInteger(ex, "value"))), false);
			egg = IntegerArgumentType.getInteger(ex, "value");
			return 0;
		}));
	}
	@Override
	int onCommand(BridgeArg<?>[] args) {
		switch (args.length) {
		case 1:
			return (exec) -> {
				return 0;
			};
		}
	}*/
	@Override
	public String getLiteralName() {
		return "ctest";
	}
	@Override
	public int onCommand(CommandInformation info) {
		ArgumentValue<?,?>[] args = info.getArgs();
		CommandSender sender = info.getSender();
		switch(args.length) {
		case 0:
			sender.sendMessage(ChatUtils.CHAT_PREFIX + "Test number: " + ChatUtils.highlightWord(""+egg) + ".");
			return 0;
		case 1:
			sender.sendMessage(ChatUtils.CHAT_PREFIX + "Test number set to " + ChatUtils.highlightWord(args[0].getValue().toString()));
			egg = (Integer) args[0].getValue();
			return 0;
		default:
			return -1;
		}
	}
	@Override
	public List<Argument<?>> getArgs() {
		return Collections.unmodifiableList(args);
	}
	
	@Override
	public IDKey getIdentifier() {
		return IDKey.conduit("testcmd");
	}
}
