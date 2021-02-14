package conduit.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.main.Main;

public class Conduit {
	private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {
    	LOGGER.info("Conduit main class successfully initialized.");
    	Main.main(args);
    }
}
