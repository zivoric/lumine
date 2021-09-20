package conduit.entity;

import conduit.command.CommandSender;
import conduit.server.Server;
import conduit.util.location.BlockLocation;
import conduit.util.location.DoubleLocation;

public interface Entity extends CommandSender {
    Server getServer();
    DoubleLocation getLocation();
    BlockLocation getBlockLocation();
}
