package lumine.bridge.client.refracts;

import lumine.prisma.refract.definition.Refract;
import lumine.prisma.refract.definition.method.InjectPoint;
import lumine.prisma.refract.definition.method.Options;
import lumine.prisma.refract.definition.method.RefractMethod;
import lumine.prisma.refract.definition.method.RefractType;
import net.minecraft.client.ClientBrandRetriever;

@Refract(ClientBrandRetriever.class)
public class ClientBrandRefract {
    @RefractMethod( name = "getClientModName",
                    type = RefractType.REPLACE,
                    at = InjectPoint.START,
                    options = @Options(cacheValue = true))
     private String clientName() {
        return "lumine";
    }

}
