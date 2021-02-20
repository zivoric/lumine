package conduit.command;

import com.mojang.brigadier.StringReader;

import conduit.entity.player.Player;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;

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
