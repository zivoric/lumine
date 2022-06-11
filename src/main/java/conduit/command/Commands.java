package conduit.command;

import conduit.util.CRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commands {
	private static final List<Command> BASE_COMMANDS = new ArrayList<Command>();
	public static final Command TEST_COMMAND = register(new TestCommand());
	public static final Command VERSION_COMMAND = register(new VersionCommand());
	public static final Command DEBUG_SEND_COMMAND = register(new DebugSendCommand());
	public static final Command TPA_COMMAND = register(new TpaCommand());
	public static final Command TPACCEPT_COMMAND = register(new TpacceptCommand());
	public static final Command TPDENY_COMMAND = register(new TpdenyCommand());

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
