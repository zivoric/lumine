package conduit.utils;

import java.util.Collection;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.server.command.ServerCommandSource;

public class CommandBridge {
	private final String literal;
	private final Collection<ArgumentCommandNode<ServerCommandSource,?>> arguments;
	@SuppressWarnings("unchecked")
	public CommandBridge(LiteralArgumentBuilder<ServerCommandSource> brigadier) {
		literal = brigadier.getLiteral();
		brigadier.getArguments().forEach(node -> {
			if (node instanceof ArgumentCommandNode)
				arguments.add((ArgumentCommandNode<ServerCommandSource, ?>)node);
		});
	}
	public CommandBridge(String literal) {
		
	}
	public void node() {
		arguments.forEach(node -> {
		});
	}
}
