package shiroroku.dmcloot.Configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientConfiguration {

    public static final ForgeConfigSpec CLIENTCONFIG;

    static {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

        CLIENTCONFIG = clientBuilder.build();
    }
}
