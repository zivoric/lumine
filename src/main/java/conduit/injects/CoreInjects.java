package conduit.injects;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
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
import conduit.injection.util.MethodInfo;
import conduit.modification.ModManager;
import conduit.modification.exception.ModManagementException;
import conduit.util.CRegistry;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

import java.net.Proxy;
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
                }/*,
                new VoidInjector<MinecraftServer>(MethodInfo.fromTypes("<init>",
                        void.class, Thread.class, DynamicRegistryManager.Impl.class, LevelStorage.Session.class,
                        SaveProperties.class, ResourcePackManager.class, Proxy.class, DataFixer.class,
                        ServerResourceManager.class, MinecraftSessionService.class, GameProfileRepository.class, UserCache.class,
                        WorldGenerationProgressListenerFactory.class), InjectProperties.Context.INSTANCE) {
                    @InvokeInjection(InjectProperties.Point.START)
                    public static <S extends MinecraftServer> void initializeMods(Thread arg0, DynamicRegistryManager.Impl arg1, LevelStorage.Session arg2, SaveProperties arg3, ResourcePackManager arg4, Proxy arg5, DataFixer arg6, ServerResourceManager arg7,  MinecraftSessionService arg8,  GameProfileRepository arg9,  UserCache arg10, WorldGenerationProgressListenerFactory arg11) {
                        try {
                            ModManager loader = ModManager.create();
                            loader.getModManager();
                            loader.prepareServerMods();
                            loader.initializeServerMods();
                        } catch (ModManagementException e) {
                            Conduit.LOGGER.error("Error while initializing server mods", e);
                        }
                    }
                }*/
        ));
        INJECTORS.add(new ClassInjector<>(
                new VoidInjector<net.minecraft.server.Main>(net.minecraft.server.Main::main) {
                    @InvokeInjection(InjectProperties.Point.START)
                    public static void initializeMods(String[] args) {
                        try {
                            ModManager loader = ModManager.create();
                            loader.getModManager();
                            loader.prepareServerMods();
                            loader.initializeServerMods();
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
