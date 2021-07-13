package com.rpgloot.Registry;

import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Modifier.Weapon.*;
import com.rpgloot.RPGLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

import java.util.Random;

public class ModifierRegistry {

	public static final Random randomInstance = new Random();

	public enum MODIFIERS {
		FROST(5, new FrostModifier()),
		LEARNING(5, new LearningModifier()),
		SPEED(5, new SpeedModifier()),
		LIFESTEAL(5, new LifestealModifier()),
		MENDING(5, new MendingModifier());

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

	public static void applyRandomModifiersTo(Random rand, ItemStack item, IModifier.ModifierRarity rarity) {
		TextComponent original = (TextComponent) item.getHoverName();

		IModifier prefix = getRandomModifierFor(IModifier.ModifierType.Prefix, item);
		IModifier suffix = getRandomModifierFor(IModifier.ModifierType.Suffix, item);

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
			item.setHoverName(newname.withStyle(Style.EMPTY.withColor(rarity.getColor()).withItalic(false)));
		}
	}

	/**
	 * Gets a random weighted modifier of the type from the registry. Returns null if it can't find modifiers.
	 */
	public static IModifier getRandomModifierFor(IModifier.ModifierType type, ItemStack stack) {
		int total = 0;
		for (MODIFIERS mod : MODIFIERS.values()) {
			if (mod.get().canApply(stack)) {
				if (mod.get().getModifierType() == type) {
					total += mod.getWeight();
				}
			}
		}
		if (total == 0) {
			return null;
		}
		int random = randomInstance.nextInt(total) + 1;
		for (MODIFIERS mod : MODIFIERS.values()) {
			if (mod.get().getModifierType() == type) {
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
	public static IModifier.ModifierRarity getRandomWeightedRarity(Random rand, int[] weights) {
		if (weights.length != IModifier.ModifierRarity.values().length) {
			RPGLoot.LOGGER.error("(rpgloot.randomize): (rpgloot.rarity_weights): Rarity values and given weights do not correlate!");
			return null;
		}

		int total = 0;
		int index = 0;
		for (IModifier.ModifierRarity rartiy : IModifier.ModifierRarity.values()) {
			total += weights[index];
			index++;
		}
		int random = randomInstance.nextInt(total) + 1;
		index = 0;
		for (IModifier.ModifierRarity rarity : IModifier.ModifierRarity.values()) {
			random -= weights[index];
			if (random <= 0) {
				return rarity;
			}
			index++;
		}
		return null;
	}

	public static IModifier.ModifierRarity getRandomRarity(Random rand) {
		return IModifier.ModifierRarity.values()[rand.nextInt(IModifier.ModifierRarity.values().length)];
	}
}
