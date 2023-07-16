package conduit.bridge.command;

import com.mojang.brigadier.ParseResults;
import conduit.Conduit;
import conduit.command.Command;
import conduit.command.Commands;
import conduit.util.CRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ConduitCommandManager extends CommandManager {
	public ConduitCommandManager(RegistrationEnvironment environment, CommandRegistryAccess access) {
		super(environment, access);
		Conduit.log("new conduit command manager initialized");
		Commands.get().forEach(this::updateRegistry);
		//updateRegistry();
	}
	public void updateRegistry() {
		CRegistry.COMMANDS.forEach(entry -> {
			new CommandBuilder(entry.getValue()).register(getDispatcher());
		});
	}
	public void updateRegistry(Command command) {
		CRegistry.COMMANDS.add(command.getIdentifier(), command);
		new CommandBuilder(command).register(getDispatcher());
		Conduit.log("added conduit command with literal /" + command.getLiteralName());
	}
	@Override
	public int execute(ParseResults<ServerCommandSource> commandSource, String command) {
		Conduit.log("Executing command " + command);
		return super.execute(commandSource, command);
	}
}
