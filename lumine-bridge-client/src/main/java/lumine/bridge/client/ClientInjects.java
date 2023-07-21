package lumine.bridge.client;

import lumine.bridge.modification.ModManagerCore;
import lumine.prisma.injection.ClassInjector;
import lumine.prisma.injection.FunctionInjector;
import lumine.prisma.injection.VoidInjector;
import lumine.prisma.injection.annotations.CacheValue;
import lumine.prisma.injection.annotations.InvokeInjection;
import lumine.prisma.injection.annotations.ReplaceInjection;
import lumine.prisma.injection.util.InjectProperties;
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
                        return "lumine";
                    }
                }
        ));
        // Mod loading injection
        INJECTORS.add(new ClassInjector<>(
           new VoidInjector<MinecraftClient>(MinecraftClient.class, MinecraftClient::new) {
               @InvokeInjection(InjectProperties.Point.START)
               public static void runClient(RunArgs args) {
                   ModManagerCore loader = ModManagerCore.getInstance();
                   loader.prepareClientMods();
                   loader.initializeClientMods();
               }
           }
        ));
    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
