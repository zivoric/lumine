package lumine.command;

import lumine.chat.ChatUtils;
import lumine.command.argument.Argument;
import lumine.command.argument.ArgumentValue;
import lumine.command.argument.CommandInformation;
import lumine.command.argument.types.EntityArg;
import lumine.entity.Entity;
import lumine.entity.Player;
import lumine.util.IDKey;

import java.util.*;

public class TpaCommand extends Command {
    private static final HashMap<UUID, UUID> requests = new HashMap<>();
    private static final List<Argument<?>> args = Arrays.asList(EntityArg.player("player"));

    @Override
    public String getLiteralName() {
        return "tpa";
    }

    @Override
    public IDKey getIdentifier() {
        return IDKey.lumine("tpa");
    }

    @Override
    public int onCommand(CommandInformation info) {
        ArgumentValue<?,?>[] args = info.getArgs();
        CommandSender sender = info.getSender();
        if (sender instanceof Player player) {
            switch (args.length) {
                case 0:
                    sender.sendError(ChatUtils.error("You must specify a player."));
                    return -1;
                case 1:
                default:
                    Entity entity = (Entity) ((List<?>) args[0].getValue()).get(0);
                    if (entity instanceof Player player2) {
                        requests.put(player.getUUID(), player2.getUUID());
                        sender.sendMessage(ChatUtils.message("Request sent to " + ChatUtils.highlightWord(player2.getName()) + "."));
                        player2.sendMessage(ChatUtils.message(ChatUtils.highlightWord(player.getName()) + " is requesting to teleport to you."));
                        return 0;
                    } else {
                        sender.sendError(ChatUtils.error("You must specify a player."));
                        return -1;
                    }
            }
        } else {
            sender.sendError(ChatUtils.error("Command sender is not a player."));
            return -1;
        }
    }

    public static UUID getRecipient(UUID sender) {
        return requests.get(sender);
    }

    public static void resetRecipient(UUID sender) {
        requests.remove(sender);
    }

    @Override
    public List<Argument<?>> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
