package com.dmcloot.Setup;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.DMCLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DMCLoot.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSetup {
	public static void init(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ClientSetup::onItemTooltip);
	}

	public static void onItemTooltip(ItemTooltipEvent e) {
		ItemStack item = e.getItemStack();
		if (item.hasTag() && item.getTag().contains("dmcloot.rarity") && item.getTag().getString("dmcloot.rarity") != null) {
			IModifier.Rarity rarity = null;
			for (IModifier.Rarity r : IModifier.Rarity.values()) {
				if (r.toString().equals(item.getTag().getString("dmcloot.rarity"))) {
					rarity = r;
					break;
				}
			}
			if (rarity == null) {
				rarity = IModifier.Rarity.Common;
				DMCLoot.LOGGER.error("(onItemTooltip): (dmcloot.rarity): Rarity specified in itemstack NBT does not exist!");
			}

			//Special, for modpack
			if (e.getToolTip().get(1).getString().equals("Common")) {
				e.getToolTip().remove(1);
			}
			//====

			java.awt.Color darker = customDarker(new java.awt.Color(rarity.getColor().getColor()));
			e.getToolTip().add(1, new TranslationTextComponent("rarity.dmcloot." + rarity).setStyle(Style.EMPTY.withColor(Color.fromRgb(darker.getRGB()))));
		}
	}

	private static java.awt.Color customDarker(java.awt.Color color) {
		return new java.awt.Color(Math.max((int) (color.getRed() * (float) 0.68), 0), Math.max((int) (color.getGreen() * (float) 0.68), 0), Math.max((int) (color.getBlue() * (float) 0.68), 0), color.getAlpha());
	}

}
