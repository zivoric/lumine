package conduit.netmc.command;

import net.minecraft.server.command.CommandManager;

public class ConduitCommandManager extends CommandManager {
	
	public ConduitCommandManager(RegistrationEnvironment environment) {
		super(environment);
		registerAllCommands(this);
	}
	public static void registerAllCommands(ConduitCommandManager manager) {
		Commands.get().forEach(command -> {
			command.register(manager.getDispatcher());
		});
	}
}
