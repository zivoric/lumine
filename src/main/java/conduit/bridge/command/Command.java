package conduit.bridge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import conduit.chat.ChatUtils;
import conduit.util.IDKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command {
	
	public final void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(buildLiteral());
	}
	public abstract String getLiteralName();
	public abstract IDKey getIdentifier();
	public abstract int onCommand(CommandInformation info);
	public abstract List<Argument<?>> getArgs();
	private LiteralArgumentBuilder<ServerCommandSource> buildLiteral() {
		List<Argument<?>> args = new ArrayList<>(getArgs());
		LiteralArgumentBuilder<ServerCommandSource> base = LiteralArgumentBuilder.literal(getLiteralName());
		LiteralArgumentBuilder<ServerCommandSource> builder = base.executes(context -> {
			return onCommand(new CommandInformation(this, CommandInformation.getSender(context.getSource()), new ArgumentValue<?,?>[0]));
		});
		
		List<Argument<?>> reversedArgs = new ArrayList<Argument<?>>(args);
		Collections.reverse(reversedArgs);
		RequiredArgumentBuilder<ServerCommandSource, ?> currentArg = null;
		for (Argument<?> arg : reversedArgs) {
			RequiredArgumentBuilder<ServerCommandSource, ?> req = RequiredArgumentBuilder.argument(arg.getIdentifier(), arg.toBrigadier());
			if (currentArg==null) {
				currentArg = req.executes(context -> {
					return executeCommand(context, arg, args);
				});
			} else {
				currentArg = req.executes(context -> {
					return executeCommand(context, arg, args);
				}).then(currentArg);
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
		try {
			for (Argument<?> a : args) {
				vals.add(new ArgumentValue(a, context.getArgument(a.getIdentifier(), a.getType())));
				if (a.equals(arg)) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			context.getSource().sendError(new LiteralText(ChatUtils.error("Argument error, this is a conduit bug!")));
			return 0;
		}
		try {
			return onCommand(new CommandInformation(this, CommandInformation.getSender(context.getSource()), vals.toArray(new ArgumentValue<?,?>[0])));
		} catch (Exception e) {
			e.printStackTrace();
			context.getSource().sendError(new LiteralText(ChatUtils.error()));
			return 0;
		}
	}
}
