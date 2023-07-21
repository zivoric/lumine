package lumine.bridge.command;

import com.mojang.brigadier.ParseResults;
import lumine.Lumine;
import lumine.command.Command;
import lumine.command.Commands;
import lumine.util.LRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class LumineCommandManager extends CommandManager {
	public LumineCommandManager(RegistrationEnvironment environment, CommandRegistryAccess access) {
		super(environment, access);
		Lumine.getLogger().info("new lumine command manager initialized");
		Commands.get().forEach(this::updateRegistry);
		//updateRegistry();
	}
	public void updateRegistry() {
		LRegistry.COMMANDS.forEach(entry -> {
			new CommandBuilder(entry.getValue()).register(getDispatcher());
		});
	}
	public void updateRegistry(Command command) {
		LRegistry.COMMANDS.add(command.getIdentifier(), command);
		new CommandBuilder(command).register(getDispatcher());
		Lumine.getLogger().info("added lumine command with literal /" + command.getLiteralName());
	}
	@Override
	public int execute(ParseResults<ServerCommandSource> commandSource, String command) {
		Lumine.getLogger().info("Executing command " + command);
		return super.execute(commandSource, command);
	}
}
