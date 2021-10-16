package conduit.injects;

import conduit.bridge.command.CommandBuilder;
import conduit.command.Command;
import conduit.command.Commands;
import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.VoidInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.util.InjectProperties;
import conduit.Conduit;
import conduit.injection.util.MethodGrabber;
import conduit.util.CRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public final class CoreInjects {
    private final List<ClassInjector<?>> INJECTORS = new LinkedList<>();
    public CoreInjects() {
        // CommandManager modifications
        INJECTORS.add(new ClassInjector<>(
                new VoidInjector<CommandManager>(CommandManager::new) {
                    @InvokeInjection(InjectProperties.Point.RETURN)
                    static void init(CommandManager instance, CommandManager.RegistrationEnvironment commandEnvironment) {
                        Commands.get().forEach(cmd -> updateRegistry(instance, cmd));
                    }

                    private static void updateRegistry(CommandManager instance, Command command) {
                        CRegistry.COMMANDS.add(command.getIdentifier(), command);
                        new CommandBuilder(command).register(instance.getDispatcher());
                        Conduit.log("added conduit command with literal /" + command.getLiteralName());
                    }
                }
        ));
        // MinecraftServer modifications
        INJECTORS.add(new ClassInjector<>(
                new FunctionInjector<MinecraftServer>(MinecraftServer.class, MinecraftServer::getServerModName) {
                    @ReplaceInjection
                    @CacheValue
                    public static String serverName(MinecraftServer instance) {
                        return "conduit";
                    }
                },
                new FunctionInjector<>((MethodGrabber.Args1<Function<Thread, ? extends MinecraftServer>, ? extends MinecraftServer>) MinecraftServer::startServer) {

                }
        ));

    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
