package shiroroku.dmcloot;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.dmcloot.Configuration.CommonConfiguration;
import shiroroku.dmcloot.Modifier.ModifierRarity;
import shiroroku.dmcloot.Util.ModifierHelper;

@Mod.EventBusSubscriber(modid = DMCLoot.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
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
