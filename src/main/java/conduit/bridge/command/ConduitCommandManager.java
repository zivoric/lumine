package conduit.bridge.command;

import conduit.command.Commands;
import conduit.main.Conduit;
import conduit.util.CRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ConduitCommandManager extends CommandManager {
	public ConduitCommandManager(RegistrationEnvironment environment) {
		super(environment);
		Conduit.log("new conduit command manager initialized");
		Commands.get().forEach(cmd -> {
			updateRegistry(cmd);
		});
		//updateRegistry();
	}
	public void updateRegistry() {
		CRegistry.COMMANDS.forEach(entry -> {
			entry.getValue().register(getDispatcher());
		});
	}
	public void updateRegistry(Command command) {
		CRegistry.COMMANDS.add(command.getIdentifier(), command);
		command.register(getDispatcher());
		Conduit.log("added conduit command with literal /" + command.getLiteralName());
	}
	@Override
	public int execute(ServerCommandSource commandSource, String command) {
		Conduit.log("Executing command " + command);
		return super.execute(commandSource, command);
	}
}
