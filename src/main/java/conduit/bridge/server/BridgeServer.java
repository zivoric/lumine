package conduit.bridge.server;

import conduit.server.PlayerHandler;
import conduit.server.Server;
import net.minecraft.server.MinecraftServer;

public class BridgeServer implements Server {
	private final MinecraftServer minecraftServer;
	public BridgeServer(MinecraftServer minecraft) {
		minecraftServer = minecraft;
	}
	
	@Override
	public PlayerHandler getPlayerHandler() {
		return new BridgePlayerHandler(minecraftServer.getPlayerManager());
	}

}
