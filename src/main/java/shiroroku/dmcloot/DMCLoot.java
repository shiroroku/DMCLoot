package shiroroku.dmcloot;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shiroroku.dmcloot.Configuration.ClientConfiguration;
import shiroroku.dmcloot.Configuration.CommonConfiguration;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ItemRegistry;
import shiroroku.dmcloot.Util.ItemModifierAutoRandomizer;

import java.util.Random;

@Mod(DMCLoot.MODID)
public class DMCLoot {

    public static final String MODID = "dmcloot";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random randomInstance = new Random();
    public static final CreativeModeTab creativeTab = new CreativeModeTab(DMCLoot.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.ESSENCE_MYTHIC.get());
        }
    };

    public DMCLoot() {
        ItemRegistry.init();
        Events.init();
        AttributeRegistry.init();
        ItemModifierAutoRandomizer.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.COMMONCONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfiguration.CLIENTCONFIG);
    }
}
