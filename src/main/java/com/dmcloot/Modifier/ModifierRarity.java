package com.dmcloot.Modifier;

import com.dmcloot.Configuration.CommonConfiguration;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

public enum ModifierRarity {
	COMMON(TextFormatting.WHITE, "common"),
	UNCOMMON(TextFormatting.GREEN, "uncommon"),
	RARE(TextFormatting.BLUE, "rare"),
	EPIC(TextFormatting.DARK_PURPLE, "epic"),
	LEGENDARY(TextFormatting.GOLD, "legendary"),
	MYTHIC(TextFormatting.RED, "mythic");

	private final TextFormatting color;
	private final String name;

	ModifierRarity(TextFormatting color, String name) {
		this.color = color;
		this.name = name;
	}

	public static int getWeight(ModifierRarity rarity) {
		switch (rarity){
			case COMMON:
				return CommonConfiguration.COMMON_WEIGHT.get();
			case UNCOMMON:
				return CommonConfiguration.UNCOMMON_WEIGHT.get();
			case RARE:
				return CommonConfiguration.RARE_WEIGHT.get();
			case EPIC:
				return CommonConfiguration.EPIC_WEIGHT.get();
			case LEGENDARY:
				return CommonConfiguration.LEGENDARY_WEIGHT.get();
			case MYTHIC:
				return CommonConfiguration.MYTHIC_WEIGHT.get();
		}
		return 0;
	}

	public TextFormatting getColor() {
		return this.color;
	}

	public String toString() {
		return this.name;
	}
}
