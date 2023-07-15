package shiroroku.dmcloot;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shiroroku.dmcloot.Configuration.CommonConfiguration;
import shiroroku.dmcloot.Item.AffixedMetalItemColor;
import shiroroku.dmcloot.Item.EssenceItemColor;
import shiroroku.dmcloot.Modifier.ModifierRarity;
import shiroroku.dmcloot.Registry.ItemRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;
import shiroroku.dmcloot.Util.ModifierHelper;

@Mod.EventBusSubscriber(modid = DMCLoot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ClientSetup::onItemTooltip);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register(new EssenceItemColor(), ItemRegistry.ESSENCE_COMMON.get(), ItemRegistry.ESSENCE_UNCOMMON.get(), ItemRegistry.ESSENCE_RARE.get(), ItemRegistry.ESSENCE_EPIC.get(), ItemRegistry.ESSENCE_LEGENDARY.get(), ItemRegistry.ESSENCE_MYTHIC.get());
        for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
            event.getItemColors().register(new AffixedMetalItemColor(), modifier.get().getAffixedMetal());
        }
    }

    public static void onItemTooltip(ItemTooltipEvent e) {
        ItemStack item = e.getItemStack();
        if (ModifierHelper.hasAnyModifier(item)) {
            ModifierRarity rarity = ModifierHelper.getItemRarity(item);
            TextColor rarityColor;
            if (CommonConfiguration.DIM_RARITY_TOOLTIP.get()) {
                rarityColor = TextColor.fromRgb(customDarker(new java.awt.Color(rarity.getColor().getColor())).getRGB());
            } else {
                rarityColor = TextColor.fromRgb(new java.awt.Color(rarity.getColor().getColor()).getRGB());
            }
            e.getToolTip().add(1, Component.translatable("rarity.dmcloot." + rarity).setStyle(Style.EMPTY.withColor(rarityColor)));
        }
    }

    /**
     * Darkens the color by 0.68, instead of default 0.70
     */
    private static java.awt.Color customDarker(java.awt.Color color) {
        return new java.awt.Color(Math.max((int) (color.getRed() * (float) 0.68), 0), Math.max((int) (color.getGreen() * (float) 0.68), 0), Math.max((int) (color.getBlue() * (float) 0.68), 0), color.getAlpha());
    }

}
