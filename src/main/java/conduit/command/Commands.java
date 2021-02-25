package conduit.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import conduit.util.CRegistry;

public class Commands {
	private static final List<Command> BASE_COMMANDS = new ArrayList<Command>();
	public static final Command TEST_COMMAND = register(new TestCommand());
	public static final Command VERSION_COMMAND = register(new VersionCommand());
	private static Command register(Command command) {
		boolean canPut = true;
		for (Command c : BASE_COMMANDS) {
			if (command.getClass().isInstance(c)) {
				canPut = false;
			}
		}
		if (canPut) {
			BASE_COMMANDS.add(command);
			CRegistry.COMMANDS.add(command.getIdentifier(), command);
		}
		return command;
	}
	public static List<Command> get() {
		return Collections.unmodifiableList(BASE_COMMANDS);
	}
}
