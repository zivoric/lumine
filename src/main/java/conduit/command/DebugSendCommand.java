package conduit.command;

import conduit.chat.ChatColors;
import conduit.chat.ChatUtils;
import conduit.command.argument.Argument;
import conduit.command.argument.ArgumentValue;
import conduit.command.argument.CommandInformation;
import conduit.command.argument.types.EntityArg;
import conduit.command.argument.types.StringArg;
import conduit.command.argument.types.StringArg.StrType;
import conduit.entity.Player;
import conduit.util.IDKey;

import java.util.Arrays;
import java.util.List;

public class DebugSendCommand extends Command {
	private static final List<Argument<?>> args = Arrays.asList(StringArg.stringArg("type", StrType.SINGLE), EntityArg.player("player")/*StringArg.stringArg("player", StrType.SINGLE)*/, StringArg.stringArg("message", StrType.GREEDY));
	
	@Override
	public String getLiteralName() {
		return "csend";
	}

	@Override
	public IDKey getIdentifier() {
		return IDKey.conduit("csend");
	}

	@Override
	public int onCommand(CommandInformation info) {
		ArgumentValue<?,?>[] args = info.getArgs();
		CommandSender sender = info.getSender();
		switch(args.length) {
		case 0:
		case 1:
		case 2:
			break;
		default:
			String msgType = (String)args[0].getValue();
			//String player = (String)args[1].getValue();
			String message = (String)args[2].getValue();
			Player targetPlayer = (Player)((List<?>)args[1].getValue()).get(0);//sender.getServer().getPlayer(player);
			if (targetPlayer == null) {
				sender.sendError(ChatUtils.error("This player does not exist."));
				return -1;
			}
			if (msgType.equalsIgnoreCase("message"))
				targetPlayer.sendMessage(ChatColors.convertColorCodes(message));
			else if (msgType.equalsIgnoreCase("error"))
				targetPlayer.sendError(ChatColors.convertColorCodes(message));
			else
				break;
			return 0;
		}
		sender.sendError(ChatUtils.error("Usage: /" + getLiteralName() + " <message|error> <player> <message>"));
		return -1;
	}

	@Override
	public List<Argument<?>> getArgs() {
		return args;
	}

}
