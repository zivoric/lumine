package conduit.client;

import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.VoidInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.InvokeInjection;
import conduit.injection.annotations.ReplaceInjection;
import conduit.injection.util.InjectProperties;
import conduit.modification.ModManager;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;

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
           new VoidInjector<MinecraftClient>(MinecraftClient.class, MinecraftClient::run) {
               @InvokeInjection(InjectProperties.Point.START)
               public static void runClient() {
                   ModManager.getInstance().prepareClientMods();
                   ModManager.getInstance().initializeClientMods();
               }
           }
        ));
    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
