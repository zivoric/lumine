package conduit.entity;

import conduit.command.CommandSender;
import conduit.server.Server;
import conduit.util.location.BlockLocation;
import conduit.util.location.DoubleLocation;

import java.util.UUID;

public interface Entity extends CommandSender {
    Server getServer();
    DoubleLocation getLocation();
    BlockLocation getBlockLocation();

    UUID getUUID();

    void teleport(double x, double y, double z);

    void teleport(DoubleLocation location);

    void teleport(Entity e);
}
