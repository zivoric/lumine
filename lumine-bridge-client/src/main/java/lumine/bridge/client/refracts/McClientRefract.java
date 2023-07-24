package lumine.bridge.client.refracts;

import lumine.modification.ModManagerCore;
import lumine.prisma.refract.definition.Refract;
import lumine.prisma.refract.definition.method.InjectPoint;
import lumine.prisma.refract.definition.method.RefractMethod;
import lumine.prisma.refract.definition.method.RefractType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

@Refract(MinecraftClient.class)
public class McClientRefract {
    @RefractMethod(name = "<init>", type = RefractType.INSERT, at = InjectPoint.START)
    private void runClient(RunArgs args) {
        ModManagerCore loader = ModManagerCore.getInstance();
        loader.prepareClientMods();
        loader.initializeClientMods();
    }
}
