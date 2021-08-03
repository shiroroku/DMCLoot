package com.dmcloot.Registry;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Modifier.Prefix.*;
import com.dmcloot.Modifier.Suffix.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

import java.util.Random;

public class ModifierRegistry {

	public static final Random randomInstance = new Random();

	public enum MODIFIERS {
		FROST(new FrostModifier()),
		FIRE(new FireModifier()),
		LEARNING(new LearningModifier()),
		SPEED(new SpeedModifier()),
		LIFESTEAL(new LifestealModifier()),
		MENDING(new MendingModifier()),
		SWIFTNESS(new SwiftnessModifier()),
		GUARDING(new GuardingModifier()),
		REACHING(new ReachingModifier()),
		REGENERATION(new RegenerationModifier()),
		AQUATIC(new AquaticModifier());

		private final ModifierBase modifier;

		MODIFIERS(ModifierBase m) {
			this.modifier = m;
		}

		public ModifierBase get() {
			return this.modifier;
		}

	}

	public static void applyRandomModifiersTo(ItemStack item, IModifier.Rarity rarity) {
		TextComponent original = (TextComponent) item.getHoverName();

		ModifierBase prefix = getRandomModifierFor(IModifier.Affix.Prefix, item);
		ModifierBase suffix = getRandomModifierFor(IModifier.Affix.Suffix, item);

		IFormattableTextComponent newname = null;

		if (prefix == null && suffix == null) {
			return;
		}
		if (prefix != null && suffix == null) {
			newname = prefix.getTranslationComponent().append(" ").append(original);
		}
		if (suffix != null && prefix == null) {
			newname = original.append(" ").append(suffix.getTranslationComponent());
		}
		if (prefix != null && suffix != null) {
			newname = prefix.getTranslationComponent().append(" ").append(original).append(" ").append(suffix.getTranslationComponent());
		}

		if (prefix != null) {
			prefix.applyWithRarity(item, rarity);
		}
		if (suffix != null) {
			suffix.applyWithRarity(item, rarity);
		}

		if (newname != null) {
			CompoundNBT tag = item.getOrCreateTag();
			tag.putString("dmcloot.rarity", rarity.toString());
			item.setTag(tag);
			item.setHoverName(newname.withStyle(Style.EMPTY.withColor(rarity.getColor()).withItalic(false)));
		}
	}

	/**
	 * Gets a random weighted modifier of the type from the registry. Returns null if it can't find modifiers.
	 */
	public static ModifierBase getRandomModifierFor(IModifier.Affix type, ItemStack stack) {
		int total = 0;
		for (MODIFIERS mod : MODIFIERS.values()) {
			if (mod.get().canApply(stack)) {
				if (mod.get().getModifierAffix() == type) {
					total += mod.get().getWeight();
				}
			}
		}
		if (total == 0) {
			return null;
		}
		int random = randomInstance.nextInt(total) + 1;
		for (MODIFIERS mod : MODIFIERS.values()) {
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
	 * Gets a random registry with the given weights
	 */
	public static IModifier.Rarity getRandomRarityWithWeights(Random rand, int[] weights) {
		if (weights.length != IModifier.Rarity.values().length) {
			DMCLoot.LOGGER.error("(dmcloot.randomize): (dmcloot.rarity_weights): Rarity values and given weights do not correlate!");
			return null;
		}

		int total = 0;
		int index = 0;
		for (IModifier.Rarity ignored : IModifier.Rarity.values()) {
			total += weights[index];
			index++;
		}
		int random = rand.nextInt(total) + 1;
		index = 0;
		for (IModifier.Rarity rarity : IModifier.Rarity.values()) {
			random -= weights[index];
			if (random <= 0) {
				return rarity;
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets a random weighted rarity of the type from the registry
	 */
	public static IModifier.Rarity getRandomWeightedRarity(Random rand) {
		int total = 0;
		for (IModifier.Rarity rarity : IModifier.Rarity.values()) {
			total += rarity.getWeight();
		}
		int random = rand.nextInt(total) + 1;
		for (IModifier.Rarity rarity : IModifier.Rarity.values()) {
			random -= rarity.getWeight();
			if (random <= 0) {
				return rarity;
			}
		}
		return null;
	}

}
