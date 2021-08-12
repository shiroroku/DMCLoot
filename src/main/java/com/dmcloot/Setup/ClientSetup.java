package com.dmcloot.Setup;

import com.dmcloot.DMCLoot;
import com.dmcloot.Item.EssenceItemColor;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = DMCLoot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

	public static void init(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ClientSetup::onItemTooltip);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register(new EssenceItemColor(), ItemRegistry.ESSENCE_COMMON.get(), ItemRegistry.ESSENCE_UNCOMMON.get(), ItemRegistry.ESSENCE_RARE.get(), ItemRegistry.ESSENCE_EPIC.get(), ItemRegistry.ESSENCE_LEGENDARY.get(), ItemRegistry.ESSENCE_MYTHIC.get());
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
			if (e.getToolTip().size() >= 2) {
				if (e.getToolTip().get(1).getString().equals("Common")) {
					e.getToolTip().remove(1);
				}
			}
			//====

			e.getToolTip().add(1, new TranslationTextComponent("rarity.dmcloot." + rarity).setStyle(Style.EMPTY.withColor(Color.fromRgb(customDarker(new java.awt.Color(rarity.getColor().getColor())).getRGB()))));
		}
	}

	/**
	 * Darkens the color by 0.68, instead of default 0.70
	 */
	private static java.awt.Color customDarker(java.awt.Color color) {
		return new java.awt.Color(Math.max((int) (color.getRed() * (float) 0.68), 0), Math.max((int) (color.getGreen() * (float) 0.68), 0), Math.max((int) (color.getBlue() * (float) 0.68), 0), color.getAlpha());
	}

}
