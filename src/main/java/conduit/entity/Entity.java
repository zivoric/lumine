package conduit.entity;

import conduit.command.CommandSender;
import conduit.server.Server;

public abstract class Entity implements CommandSender {
    public abstract Server getServer();
}
