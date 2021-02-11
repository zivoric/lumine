package conduit.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.main.Main;
import net.minecraft.server.command.CommandManager;

public class Conduit {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final CommandManager cmdManager = new CommandManager(CommandManager.RegistrationEnvironment.field_25419);
    public static void main(String[] args) {
    	ZCommand.register(cmdManager.getDispatcher());
    	LOGGER.info("Conduit main class successfully initialized.");
    	Main.main(args);
    }
}
