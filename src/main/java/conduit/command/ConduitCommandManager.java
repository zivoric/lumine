package conduit.command;

import conduit.main.Conduit;
import conduit.util.CRegistry;
import net.minecraft.server.command.CommandManager;

public class ConduitCommandManager extends CommandManager {
	public ConduitCommandManager(RegistrationEnvironment environment) {
		super(environment);
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
	}
}
