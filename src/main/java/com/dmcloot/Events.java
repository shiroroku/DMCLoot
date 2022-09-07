package com.dmcloot;

import com.dmcloot.Item.AffixedMetalItem;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Modifier.ModifierRarity;
import com.dmcloot.Registry.ModifierRegistry;
import com.dmcloot.Util.ModifierHelper;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Events {

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(Events::onItemAttributeModifier);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Events::sortItemAttributes);
		MinecraftForge.EVENT_BUS.addListener(Events::onAnvilUpdate);

		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			modifier.get().handleEventRegistry();
		}
	}

	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack leftItem = event.getLeft();
		ItemStack rightItem = event.getRight();

		if (!leftItem.isEmpty() && !rightItem.isEmpty()) {
			if (rightItem.getItem() instanceof AffixedMetalItem) {
				AffixedMetalItem affixedMetal = (AffixedMetalItem) rightItem.getItem();
				if (affixedMetal.getModifier().canApply(leftItem)) {

					List<ModifierBase> previousModifiers = new ArrayList<>();

					if (affixedMetal.getModifier().getModifierAffix() == IModifier.Affix.Prefix) {
						for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(leftItem)) {
							if (modifier.get().getModifierAffix() == IModifier.Affix.Suffix) {
								previousModifiers.add(modifier.get());
							}
						}
					} else {
						for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(leftItem)) {
							if (modifier.get().getModifierAffix() == IModifier.Affix.Prefix) {
								previousModifiers.add(modifier.get());
							}
						}
					}

					ItemStack outputItem = new ItemStack(leftItem.getItem());
					ModifierRarity itemRarity = ModifierHelper.getItemRarity(leftItem);

					EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(leftItem), outputItem);
					if (outputItem.isDamageableItem()) {
						outputItem.setDamageValue(leftItem.getDamageValue());
					}

					outputItem.getTag().putBoolean("rpgloot.randomize", false);
					affixedMetal.getModifier().applyWithRarity(outputItem, itemRarity);

					for (ModifierBase modifier : previousModifiers) {
						modifier.applyWithRarity(outputItem, itemRarity);
					}

					ModifierHelper.renameItemFromModifiers(outputItem, itemRarity);

					event.setOutput(outputItem);
					event.setCost(1);
					event.setMaterialCost(1);
				}
			}
		}
	}

	public static void sortItemAttributes(ItemAttributeModifierEvent e) {
		if (e.getModifiers().isEmpty()) {
			return;
		}

		if (ModifierHelper.hasAnyModifier(e.getItemStack())) {
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
