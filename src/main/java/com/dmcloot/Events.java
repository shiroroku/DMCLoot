package com.dmcloot;

import com.dmcloot.Registry.ModifierRegistry;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Comparator;

public class Events {

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(Events::onItemAttributeModifier);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Events::sortItemAttributes);

		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			modifier.get().handleEventRegistry();
		}

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
