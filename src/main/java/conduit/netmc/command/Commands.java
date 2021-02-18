package conduit.netmc.command;

import java.util.ArrayList;
import java.util.List;

public class Commands {
	private static final List<Command> BASE_COMMANDS = new ArrayList<Command>();
	public static final Command TEST_COMMAND = register(new TestCommand());
	public static final Command VERSION_COMMAND = register(new VersionCommand());
	public static Command register(Command command) {
		boolean canPut = true;
		for (Command c : BASE_COMMANDS) {
			if (command.getClass().isInstance(c)) {
				canPut = false;
			}
		}
		if (canPut)
			BASE_COMMANDS.add(command);
		return command;
	}
	public static List<Command> get() {
		return BASE_COMMANDS;
	}
}
