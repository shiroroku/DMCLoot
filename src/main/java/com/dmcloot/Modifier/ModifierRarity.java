package com.dmcloot.Modifier;

import com.dmcloot.Configuration.CommonConfiguration;
import net.minecraft.util.text.TextFormatting;

public enum ModifierRarity {
	COMMON(TextFormatting.WHITE, "common", CommonConfiguration.COMMON_WEIGHT.get()),
	UNCOMMON(TextFormatting.GREEN, "uncommon", CommonConfiguration.UNCOMMON_WEIGHT.get()),
	RARE(TextFormatting.BLUE, "rare", CommonConfiguration.RARE_WEIGHT.get()),
	EPIC(TextFormatting.DARK_PURPLE, "epic", CommonConfiguration.EPIC_WEIGHT.get()),
	LEGENDARY(TextFormatting.GOLD, "legendary", CommonConfiguration.LEGENDARY_WEIGHT.get()),
	MYTHIC(TextFormatting.RED, "mythic", CommonConfiguration.MYTHIC_WEIGHT.get());

	private final TextFormatting color;
	private final String name;
	private final int weight;

	ModifierRarity(TextFormatting color, String name, int weight) {
		this.color = color;
		this.name = name;
		this.weight = weight;
	}

	public int getWeight() {
		return this.weight;
	}

	public TextFormatting getColor() {
		return this.color;
	}

	public String toString() {
		return this.name;
	}
}
