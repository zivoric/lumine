package lumine.entity;

import lumine.command.CommandSender;
import lumine.server.Server;
import lumine.util.location.BlockLocation;
import lumine.util.location.DoubleLocation;

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
