package conduit.client;

import conduit.injection.ClassInjector;
import conduit.injection.FunctionInjector;
import conduit.injection.annotations.CacheValue;
import conduit.injection.annotations.ReplaceInjection;
import net.minecraft.client.ClientBrandRetriever;

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
    }
    public List<ClassInjector<?>> getInjectors() {
        return Collections.unmodifiableList(INJECTORS);
    }
}
