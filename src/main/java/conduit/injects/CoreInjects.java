package conduit.injects;

import conduit.bridge.command.CommandBuilder;
import conduit.command.Command;
import conduit.command.Commands;
import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.VoidInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.PassInstance;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.util.InjectProperties;
import conduit.Conduit;
import conduit.injection.util.MethodGrabber;
import conduit.modification.ModManagementLoader;
import conduit.modification.ModManager;
import conduit.modification.exception.ModManagementException;
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
                    @PassInstance
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
                    public static String serverName() {
                        return "conduit";
                    }
                },
                new FunctionInjector<MinecraftServer>((MethodGrabber.Args1<Function<Thread, ? extends MinecraftServer>, ? extends MinecraftServer>) MinecraftServer::startServer) {
                    @InvokeInjection(InjectProperties.Point.START)
                    public static <S extends MinecraftServer> void initializeMods(Function<Thread,S> func) {
                        try {
                            ModManagementLoader loader = ModManagementLoader.create();
                            loader.getModManager();
                            loader.invoke("prepareServerMods");
                            loader.invoke("initializeServerMods");
                        } catch (ModManagementException e) {
                            Conduit.LOGGER.error("Error while initializing server mods", e);
                        }
                    }
                }
        ));

    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
