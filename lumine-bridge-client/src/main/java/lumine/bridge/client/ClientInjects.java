package lumine.bridge.client;

import lumine.modification.ModManagerCore;
import lumine.prisma.refract.lambda.ClassInjector;
import lumine.prisma.refract.lambda.FunctionInjector;
import lumine.prisma.refract.lambda.VoidInjector;
import lumine.prisma.refract.lambda.annotations.props.CacheValue;
import lumine.prisma.refract.lambda.annotations.types.InvokeInjection;
import lumine.prisma.refract.lambda.annotations.types.ReplaceInjection;
import lumine.prisma.refract.definition.method.InjectPoint;
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
               @InvokeInjection(InjectPoint.START)
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
