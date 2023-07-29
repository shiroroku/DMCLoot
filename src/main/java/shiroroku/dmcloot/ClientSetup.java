package shiroroku.dmcloot;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.dmcloot.Item.AffixedMetalItemColor;
import shiroroku.dmcloot.Item.EssenceItemColor;
import shiroroku.dmcloot.Registry.ItemRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

@Mod.EventBusSubscriber(modid = DMCLoot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register(new EssenceItemColor(), ItemRegistry.ESSENCE_COMMON.get(), ItemRegistry.ESSENCE_UNCOMMON.get(), ItemRegistry.ESSENCE_RARE.get(), ItemRegistry.ESSENCE_EPIC.get(), ItemRegistry.ESSENCE_LEGENDARY.get(), ItemRegistry.ESSENCE_MYTHIC.get());
        for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
            event.getItemColors().register(new AffixedMetalItemColor(), modifier.get().getAffixedMetal());
        }
    }

}
