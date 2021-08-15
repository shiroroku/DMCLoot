package com.dmcloot.Util;

import com.dmcloot.Configuration.CommonConfiguration;
import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Modifier.ModifierRarity;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModifierHelper {

	/**
	 * Gets a random weighted (from config) rarity.
	 */
	public static ModifierRarity getRandomWeightedRarity(Random rand) {
		int total = 0;
		for (ModifierRarity rarity : ModifierRarity.values()) {
			total += rarity.getWeight();
		}
		int random = rand.nextInt(total) + 1;
		for (ModifierRarity rarity : ModifierRarity.values()) {
			random -= rarity.getWeight();
			if (random <= 0) {
				return rarity;
			}
		}
		return null;
	}

	/**
	 * Gets a random registry with the given weights
	 */
	public static ModifierRarity getRandomRarityWithWeights(Random rand, int[] weights) {
		if (weights.length != ModifierRarity.values().length) {
			DMCLoot.LOGGER.error("(dmcloot.randomize): (dmcloot.rarity_weights): Rarity values and given weights do not correlate!");
			return null;
		}

		int total = 0;
		int index = 0;
		for (ModifierRarity ignored : ModifierRarity.values()) {
			total += weights[index];
			index++;
		}
		int random = rand.nextInt(total) + 1;
		index = 0;
		for (ModifierRarity rarity : ModifierRarity.values()) {
			random -= weights[index];
			if (random <= 0) {
				return rarity;
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets a random weighted modifier of the type from the registry. Returns null if no modifier.
	 */
	public static ModifierBase getRandomModifierFor(IModifier.Affix type, ItemStack stack) {
		if (CommonConfiguration.EMPTY_AFFIX_CHANCE.get() != 0.0D && (CommonConfiguration.EMPTY_AFFIX_CHANCE.get() == 1.0D || DMCLoot.randomInstance.nextDouble() <= CommonConfiguration.EMPTY_AFFIX_CHANCE.get())) {
			return null;
		}

		int total = 0;
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			if (mod.get().canApply(stack)) {
				if (mod.get().getModifierAffix() == type) {
					total += mod.get().getWeight();
				}
			}
		}
		if (total == 0) {
			return null;
		}
		int random = DMCLoot.randomInstance.nextInt(total) + 1;
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			if (mod.get().getModifierAffix() == type) {
				if (mod.get().canApply(stack)) {
					random -= mod.get().getWeight();
					if (random <= 0) {
						return mod.get();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Applies random modifiers with rarity to the given ItemStack.
	 * Also sets item's name and rarity tag.
	 */
	public static void applyRandomModifiersTo(ItemStack item, ModifierRarity rarity) {
		TextComponent originalName = (TextComponent) item.getHoverName();
		IFormattableTextComponent newName = null;

		ModifierBase prefix = getRandomModifierFor(IModifier.Affix.Prefix, item);
		ModifierBase suffix = getRandomModifierFor(IModifier.Affix.Suffix, item);

		//If there's no possible modifiers for this item, return.
		if (prefix == null && suffix == null) {
			return;
		}

		//If there's a prefix, but no suffix, append prefix to name.
		if (prefix != null && suffix == null) {
			newName = prefix.getTranslationComponent().append(" ").append(originalName);
		}

		//If there's a suffix, but no prefix, append suffix to name.
		if (suffix != null && prefix == null) {
			newName = originalName.append(" ").append(suffix.getTranslationComponent());
		}

		//If there's both prefix and suffix, append them both to name.
		if (prefix != null && suffix != null) {
			newName = prefix.getTranslationComponent().append(" ").append(originalName).append(" ").append(suffix.getTranslationComponent());
		}

		if (prefix != null) {
			prefix.applyWithRarity(item, rarity);
		}
		if (suffix != null) {
			suffix.applyWithRarity(item, rarity);
		}

		//Update name with new name, set rarity tag.
		if (newName != null) {
			CompoundNBT tag = item.getOrCreateTag();
			tag.putString("dmcloot.rarity", rarity.toString());
			item.setTag(tag);
			item.setHoverName(newName.withStyle(Style.EMPTY.withColor(rarity.getColor()).withItalic(false)));
		}
	}

	/**
	 * Returns the rarity of the given ItemStack, Returns common by default.
	 */
	public static ModifierRarity getItemRarity(ItemStack item) {
		if (item.hasTag() && item.getTag().contains("dmcloot.rarity") && item.getTag().getString("dmcloot.rarity") != null) {
			for (ModifierRarity r : ModifierRarity.values()) {
				if (r.toString().equals(item.getTag().getString("dmcloot.rarity"))) {
					return r;
				}
			}
		}
		return ModifierRarity.COMMON;
	}

	/**
	 * Returns true if this ItemStack has any modifier.
	 */
	public static boolean hasAnyModifier(ItemStack item) {
		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			if (modifier.get().itemHasModifier(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of all modifiers that this ItemStack has.
	 */
	public static List<ModifierRegistry.MODIFIERS> getAllModifiers(ItemStack item) {
		List<ModifierRegistry.MODIFIERS> mods = new ArrayList<>();
		for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
			if (modifier.get().itemHasModifier(item)) {
				mods.add(modifier);
			}
		}
		return mods;
	}
}
