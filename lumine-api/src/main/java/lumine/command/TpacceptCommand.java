package lumine.command;

import lumine.chat.ChatColors;
import lumine.chat.ChatUtils;
import lumine.command.argument.Argument;
import lumine.command.argument.ArgumentValue;
import lumine.command.argument.CommandInformation;
import lumine.command.argument.types.EntityArg;
import lumine.entity.Player;
import lumine.util.IDKey;

import java.util.*;

public class TpacceptCommand extends Command {
    private static final HashMap<Player, Player> requests = new HashMap<>();
    private static final List<Argument<?>> args = Arrays.asList(EntityArg.player("player"));

    @Override
    public String getLiteralName() {
        return "tpaccept";
    }

    @Override
    public IDKey getIdentifier() {
        return IDKey.lumine("tpaccept");
    }

    @Override
    public int onCommand(CommandInformation info) {
        ArgumentValue<?,?>[] args = info.getArgs();
        CommandSender sender = info.getSender();
        if (sender instanceof Player player) {
            if (args.length > 0 && ((List<?>) args[0].getValue()).get(0) instanceof Player player2) {
                UUID recipient = TpaCommand.getRecipient(player2.getUUID());
                if (recipient != null && recipient.equals(player.getUUID())) {
                    TpaCommand.resetRecipient(player2.getUUID());
                    player.sendMessage(ChatUtils.message("Accepted teleport request from ") + ChatUtils.highlightWord(player2.getName()) + ".");
                    player2.sendMessage(ChatUtils.message(ChatUtils.highlightWord(player.getName()) + " has accepted your teleport request."));
                    player2.teleport(player);
                    return 0;
                } else {
                    player.sendError(ChatUtils.error("You do not have a teleport request from " + ChatUtils.highlightWord(ChatColors.RED, ChatColors.DARK_RED, player.getName())) + ".");
                    return -1;
                }
            } else {
                sender.sendError(ChatUtils.error("You must specify a player."));
                return -1;
            }
        } else {
            sender.sendError(ChatUtils.error("Command sender is not a player."));
            return -1;
        }
    }

    @Override
    public List<Argument<?>> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
