package conduit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import conduit.chat.ChatUtils;
import conduit.command.bridge.Argument;
import conduit.command.bridge.ArgumentValue;
import conduit.command.bridge.CommandInformation;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public abstract class Command {
	void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(buildLiteral());
	}
	public abstract String getLiteralName();
	public abstract int onCommand(CommandInformation info);
	public abstract List<Argument<?>> getArgs();
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private LiteralArgumentBuilder<ServerCommandSource> buildLiteral() {
		List<Argument<?>> args = new ArrayList<>(getArgs());
		LiteralArgumentBuilder<ServerCommandSource> base = LiteralArgumentBuilder.literal(getLiteralName());
		LiteralArgumentBuilder<ServerCommandSource> builder = base.executes(context -> {
			return onCommand(new CommandInformation(this, CommandInformation.getSender(context.getSource()), new ArgumentValue<?,?>[0]));
		});
		/*boolean cont = true;
		int i = 1;
		while (cont) {
			final int j = i;
			if (i<=getArgsLimit()) { 
			builder = builder.then(IntegerArgumentType.integer()executes(context -> {
				return onCommand(context, context.);
			}));
			i++;
			}
			if (i==getArgsLimit())
				cont = false;
		}*/
		for (Argument<?> arg : args) {
			RequiredArgumentBuilder<ServerCommandSource, ?> req = RequiredArgumentBuilder.argument(arg.getIdentifier(), arg.toBrigadier());
			builder = builder.then(req.executes(context -> {
				List<ArgumentValue<?,?>> vals = new ArrayList<>();
				try {
					for (Argument<?> a : args) {
						vals.add(new ArgumentValue(a, context.getArgument(a.getIdentifier(), a.getType())));
						if (a.equals(arg)) break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					context.getSource().sendError(new LiteralText(ChatUtils.error("Argument error.")));
					return 0;
				}
				try {
					return onCommand(new CommandInformation(this, CommandInformation.getSender(context.getSource()), vals.toArray(new ArgumentValue<?,?>[0])));
				} catch (Exception e) {
					e.printStackTrace();
					context.getSource().sendError(new LiteralText(ChatUtils.error()));
					return 0;
				}
			}));
		}
		return builder;
	}
}
