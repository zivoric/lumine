package conduit.client;

import conduit.Conduit;
import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.VoidInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.util.InjectProperties;
import conduit.modification.ModManagementLoader;
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
                       ModManagementLoader loader = ModManagementLoader.create();
                       loader.getModManager();
                       loader.invoke("prepareClientMods");
                       loader.invoke("initializeClientMods");
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
