package conduit.bridge.server;

import java.lang.reflect.Method;

import conduit.server.PlayerHandler;
import conduit.server.Server;
import conduit.util.ConduitUtils;
import conduit.util.ObfuscationClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;

public class BridgeServer implements Server {
	private final MinecraftServer minecraftServer;
	private static final ObfuscationClass minecraftServerClass = ConduitUtils.getCurrentMap().fromDeobf("net/minecraft/server/MinecraftServer");
	public BridgeServer(MinecraftServer minecraft) {
		minecraftServer = minecraft;
	}
	
	@Override
	public PlayerHandler getPlayerHandler() {
		try {
			Method method = MinecraftServer.class.getDeclaredMethod(minecraftServerClass.getMethodsFromDeobf("getPlayerManager").get(0).obfName());
			PlayerManager manager = (PlayerManager) method.invoke(minecraftServer);
			return new BridgePlayerHandler(manager);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
