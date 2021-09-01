package conduit.injects;

import conduit.bridge.command.Command;
import conduit.command.Commands;
import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.VoidInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.util.InjectProperties;
import conduit.main.Conduit;
import conduit.util.CRegistry;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class CoreInjectors {
    private final List<ClassInjector<?>> INJECTORS = new LinkedList<>();
    public CoreInjectors() {
        INJECTORS.add(new ClassInjector<>(
                new VoidInjector<CommandManager>(CommandManager::new) {
                    @InvokeInjection(InjectProperties.Point.START)
                    static void init(CommandManager instance, CommandManager.RegistrationEnvironment commandEnvironment) {
                        Commands.get().forEach(cmd -> {
                            updateRegistry(instance, cmd);
                        });
                    }

                    private static void updateRegistry(CommandManager instance, Command command) {
                        CRegistry.COMMANDS.add(command.getIdentifier(), command);
                        command.register(instance.getDispatcher());
                        Conduit.log("added conduit command with literal /" + command.getLiteralName());
                    }
                }
        ));
        INJECTORS.add(new ClassInjector<>(
                new FunctionInjector<MinecraftServer>(MinecraftServer.class, MinecraftServer::getServerModName) {
                    @ReplaceInjection
                    @CacheValue
                    public static String serverName(MinecraftServer instance) {
                        return "conduit";
                    }
                }
        ));
        if (Conduit.isClient()) {
            INJECTORS.add(new ClassInjector(
                    new FunctionInjector<ClientBrandRetriever>(ClientBrandRetriever::getClientModName) {
                        @ReplaceInjection
                        @CacheValue
                        public static String clientName() {
                            return "conduit";
                        }
                    }
            ));
        }
    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
