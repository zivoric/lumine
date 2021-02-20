package conduit.command;


public interface CommandSender {
public void sendMessage(String message);
public void sendError(String message);
}
