package lumine.bridge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lumine.bridge.entity.BridgePlayer;
import lumine.chat.ChatUtils;
import lumine.command.Command;
import lumine.command.CommandSender;
import lumine.command.argument.Argument;
import lumine.command.argument.ArgumentValue;
import lumine.command.argument.CommandInformation;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandBuilder {
    private final Command command;
    public CommandBuilder(Command command) {
        this.command = command;
    }
    public final void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register(buildLiteral());
    }
    private LiteralArgumentBuilder<ServerCommandSource> buildLiteral() {
        List<Argument<?>> args = new ArrayList<>(command.getArgs());
        LiteralArgumentBuilder<ServerCommandSource> base = LiteralArgumentBuilder.literal(command.getLiteralName());
        LiteralArgumentBuilder<ServerCommandSource> builder = base.executes(context -> command.onCommand(new CommandInformation(command, getSender(context.getSource()))));

        List<Argument<?>> reversedArgs = new ArrayList<>(args);
        Collections.reverse(reversedArgs);
        RequiredArgumentBuilder<ServerCommandSource, ?> currentArg = null;
        for (Argument<?> arg : reversedArgs) {
            RequiredArgumentBuilder<ServerCommandSource, ?> req = RequiredArgumentBuilder.argument(arg.getIdentifier(), ArgumentBuilder.getBuilder(arg).toBrigadier());
            if (currentArg==null) {
                currentArg = req.executes(context -> executeCommand(context, arg, args));
            } else {
                currentArg = req.executes(context -> executeCommand(context, arg, args)).then(currentArg);
            }
        }
        if (currentArg != null)
            return builder.then(currentArg);
        else
            return builder;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int executeCommand(CommandContext<ServerCommandSource> context, Argument<?> arg, List<Argument<?>> args) {
        List<ArgumentValue<?,?>> vals = new ArrayList<>();
        context.getSource();
        try {
            for (Argument<?> a : args) {
                ArgumentBuilder builder = ArgumentBuilder.getBuilder(a);
                vals.add(new ArgumentValue(a, builder.convertValue(context.getArgument(a.getIdentifier(), builder.getBrigadierClass()), context)));
                if (a.equals(arg)) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendError(MutableText.of(new LiteralTextContent(ChatUtils.error("Argument error, this is a lumine bug!"))));
            return 0;
        }
        try {
            return command.onCommand(new CommandInformation(command, getSender(context.getSource()), vals.toArray(new ArgumentValue<?,?>[0])));
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendError(MutableText.of(new LiteralTextContent(ChatUtils.error())));
            return 0;
        }
    }

    private CommandSender getSender(ServerCommandSource source) {
        if (source.getEntity() == null) {
            return new BridgeConsoleSender(source.getServer());
        } else if (source.getEntity() instanceof ServerPlayerEntity serv) {
            return new BridgePlayer(serv);
        } else {
            return null;
        }
    }
}
