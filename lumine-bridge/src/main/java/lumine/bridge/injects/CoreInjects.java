package lumine.bridge.injects;

import lumine.Lumine;
import lumine.bridge.command.CommandBuilder;
import lumine.modification.ModManagerCore;
import lumine.command.Command;
import lumine.command.Commands;
import lumine.prisma.refract.lambda.ClassInjector;
import lumine.prisma.refract.lambda.FunctionInjector;
import lumine.prisma.refract.lambda.VoidInjector;
import lumine.prisma.refract.lambda.annotations.props.CacheValue;
import lumine.prisma.refract.lambda.annotations.types.InvokeInjection;
import lumine.prisma.refract.lambda.annotations.props.PassInstance;
import lumine.prisma.refract.lambda.annotations.types.ReplaceInjection;
import lumine.prisma.refract.definition.method.InjectPoint;
import lumine.util.LRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class CoreInjects {
    private final List<ClassInjector<?>> INJECTORS = new LinkedList<>();
    public CoreInjects() {
        // CommandManager modifications
        INJECTORS.add(new ClassInjector<>(
                new VoidInjector<CommandManager>(CommandManager::new) {
                    @InvokeInjection(InjectPoint.RETURN)
                    @PassInstance
                    static void init(CommandManager instance, CommandManager.RegistrationEnvironment commandEnvironment, CommandRegistryAccess access) {
                        Commands.get().forEach(cmd -> updateRegistry(instance, cmd));
                    }

                    private static void updateRegistry(CommandManager instance, Command command) {
                        LRegistry.COMMANDS.add(command.getIdentifier(), command);
                        new CommandBuilder(command).register(instance.getDispatcher());
                        Lumine.getLogger().info("added lumine command with literal /" + command.getLiteralName());
                    }
                }
                ));
        // MinecraftServer modifications
        INJECTORS.add(new ClassInjector<>(
                new FunctionInjector<MinecraftServer>(MinecraftServer.class, MinecraftServer::getServerModName) {
                    @ReplaceInjection
                    @CacheValue
                    public static String serverName() {
                        return "lumine";
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
                            Lumine.getLogger().error("Error while initializing server mods", e);
                        }
                    }
                }*/
        ));
        INJECTORS.add(new ClassInjector<>(
                new VoidInjector<net.minecraft.server.Main>(net.minecraft.server.Main::main) {
                    @InvokeInjection(InjectPoint.START)
                    public static void initializeMods(String[] args) {
                        ModManagerCore loader = ModManagerCore.getInstance();
                        loader.prepareServerMods();
                        loader.initializeServerMods();
                    }
                }
        ));

    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
