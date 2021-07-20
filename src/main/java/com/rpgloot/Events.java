package com.rpgloot;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Comparator;

public class Events {

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(Events::onItemAttributeModifier);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Events::sortItemAttributes);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Events::onItemTooltip);

		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			modifier.get().handleEventRegistry();
		}

	}

	public static void onItemTooltip(ItemTooltipEvent e) {
		ItemStack item = e.getItemStack();
		if (item.hasTag() && item.getTag().contains("rpgloot.rarity") && item.getTag().getString("rpgloot.rarity") != null) {
			IModifier.Rarity rarity = null;
			for (IModifier.Rarity r : IModifier.Rarity.values()) {
				if (r.toString().equals(item.getTag().getString("rpgloot.rarity"))) {
					rarity = r;
					break;
				}
			}
			if (rarity == null) {
				rarity = IModifier.Rarity.Common;
				RPGLoot.LOGGER.error("(onItemTooltip): (rpgloot.rarity): Rarity specified in itemstack NBT does not exist!");
			}

			//Special, for modpack
			if(e.getToolTip().get(1).getString().equals("Common")){
				e.getToolTip().remove(1);
			}
			//====

			java.awt.Color darker = customDarker(new java.awt.Color(rarity.getColor().getColor()));
			e.getToolTip().add(1, new TranslationTextComponent("rarity.rpgloot." + rarity).setStyle(Style.EMPTY.withColor(Color.fromRgb(darker.getRGB()))));
		}
	}

	private static java.awt.Color customDarker(java.awt.Color color) {
		return new java.awt.Color(Math.max((int) (color.getRed() * (float) 0.68), 0), Math.max((int) (color.getGreen() * (float) 0.68), 0), Math.max((int) (color.getBlue() * (float) 0.68), 0), color.getAlpha());
	}

	public static void sortItemAttributes(ItemAttributeModifierEvent e) {
		if (e.getModifiers().isEmpty()) {
			return;
		}
		boolean hasaModifier = false;
		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			if (modifier.get().itemHasModifier(e.getItemStack())) {
				hasaModifier = true;
				break;
			}
		}

		if (hasaModifier) {
			Multimap<Attribute, AttributeModifier> unmodifiableModifiers = TreeMultimap.create(Comparator.comparing(ForgeRegistryEntry::getRegistryName), (value1, value2) -> {
				int nameCompare = Integer.compare(value1.getOperation().ordinal(), value2.getOperation().ordinal());
				int valueCompare = Double.compare(value2.getAmount(), value1.getAmount());
				return nameCompare == 0 ? valueCompare : nameCompare;
			});
			unmodifiableModifiers.putAll(e.getModifiers());
			ObfuscationReflectionHelper.setPrivateValue(ItemAttributeModifierEvent.class, e, unmodifiableModifiers, "unmodifiableModifiers");
		}
	}

	public static void onItemAttributeModifier(ItemAttributeModifierEvent e) {
		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			if (modifier.get().itemHasModifier(e.getItemStack())) {
				modifier.get().handleItemAttribute(e);
			}
		}
	}

}
