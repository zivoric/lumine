package lumine.command;


import lumine.server.Server;

public interface CommandSender {
    void sendMessage(String message);
    void sendError(String message);
    Server getServer();
}
