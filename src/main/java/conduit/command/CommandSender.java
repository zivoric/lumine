package conduit.command;


import conduit.server.Server;

public interface CommandSender {
    void sendMessage(String message);
    void sendError(String message);
    Server getServer();
}
