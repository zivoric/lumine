package conduit.client;

import conduit.Conduit;
import conduit.prisma.injection.ClassInjector;
import conduit.prisma.injection.FunctionInjector;
import conduit.prisma.injection.VoidInjector;
import conduit.prisma.injection.annotations.CacheValue;
import conduit.prisma.injection.annotations.InvokeInjection;
import conduit.prisma.injection.annotations.ReplaceInjection;
import conduit.prisma.injection.util.InjectProperties;
import conduit.modification.ModManager;
import conduit.modification.exception.ModManagementException;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClientInjects {
    private final List<ClassInjector<?>> INJECTORS = new LinkedList<>();
    public ClientInjects() {
        // Mod name injection
        INJECTORS.add(new ClassInjector<>(
                new FunctionInjector<ClientBrandRetriever>(ClientBrandRetriever::getClientModName) {
                    @ReplaceInjection
                    @CacheValue
                    public static String clientName() {
                        return "conduit";
                    }
                }
        ));
        // Mod loading injection
        INJECTORS.add(new ClassInjector<>(
           new VoidInjector<MinecraftClient>(MinecraftClient.class, MinecraftClient::new) {
               @InvokeInjection(InjectProperties.Point.START)
               public static void runClient(RunArgs args) {
                   try {
                       ModManager loader = ModManager.create();
                       loader.getModManager();
                       loader.prepareClientMods();
                       loader.initializeClientMods();
                   } catch (ModManagementException e) {
                       Conduit.LOGGER.error("Error while initializing client mods", e);
                   }
               }
           }
        ));
    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
