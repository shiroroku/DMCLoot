package com.dmcloot.Registry;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.Prefix.*;
import com.dmcloot.Modifier.Suffix.GuardingModifier;
import com.dmcloot.Modifier.Suffix.LearningModifier;
import com.dmcloot.Modifier.Suffix.SpeedModifier;
import com.dmcloot.Modifier.Suffix.SwiftnessModifier;
import com.dmcloot.DMCLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

import java.util.Random;

public class ModifierRegistry {

	public static final Random randomInstance = new Random();

	public enum MODIFIERS {
		FROST(Configuration.FROST_WEIGHT.get(), new FrostModifier()),
		FIRE(Configuration.FIRE_WEIGHT.get(), new FireModifier()),
		LEARNING(Configuration.LEARNING_WEIGHT.get(), new LearningModifier()),
		SPEED(Configuration.SPEED_WEIGHT.get(), new SpeedModifier()),
		LIFESTEAL(Configuration.LIFESTEAL_WEIGHT.get(), new LifestealModifier()),
		MENDING(Configuration.MENDING_WEIGHT.get(), new MendingModifier()),
		SWIFTNESS(Configuration.SWIFTNESS_WEIGHT.get(), new SwiftnessModifier()),
		GUARDING(Configuration.GUARDING_WEIGHT.get(), new GuardingModifier()),
		REACHING(Configuration.REACHING_WEIGHT.get(), new ReachingModifier());

		private final IModifier modifier;
		private final int weight;

		MODIFIERS(int weight, IModifier m) {
			this.modifier = m;
			this.weight = weight;
		}

		public IModifier get() {
			return this.modifier;
		}

		public int getWeight() {
			return this.weight;
		}

	}

	public static void applyRandomModifiersTo(ItemStack item, IModifier.Rarity rarity) {
		TextComponent original = (TextComponent) item.getHoverName();

		IModifier prefix = getRandomModifierFor(IModifier.Affix.Prefix, item);
		IModifier suffix = getRandomModifierFor(IModifier.Affix.Suffix, item);

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
	public static IModifier getRandomModifierFor(IModifier.Affix type, ItemStack stack) {
		int total = 0;
		for (MODIFIERS mod : MODIFIERS.values()) {
			if (mod.get().canApply(stack)) {
				if (mod.get().getModifierAffix() == type) {
					total += mod.getWeight();
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
					random -= mod.getWeight();
					if (random <= 0) {
						return mod.get();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets a random weighted rarity of the type from the registry
	 */
	public static IModifier.Rarity getRandomWeightedRarity(Random rand, int[] weights) {
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

	public static IModifier.Rarity getRandomRarity(Random rand) {
		return IModifier.Rarity.values()[rand.nextInt(IModifier.Rarity.values().length)];
	}
}