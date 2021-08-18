package com.dmcloot.Configuration;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonConfiguration {

	public static final ForgeConfigSpec COMMONCONFIG;

	public static final ForgeConfigSpec.BooleanValue MODIFY_ALL;
	public static final ForgeConfigSpec.DoubleValue GLOBAL_STRENGTH_MODIFIER;
	public static final ForgeConfigSpec.DoubleValue EMPTY_AFFIX_CHANCE;
	public static final ForgeConfigSpec.BooleanValue DIM_RARITY_TOOLTIP;

	public static final ForgeConfigSpec.IntValue COMMON_WEIGHT;
	public static final ForgeConfigSpec.IntValue UNCOMMON_WEIGHT;
	public static final ForgeConfigSpec.IntValue RARE_WEIGHT;
	public static final ForgeConfigSpec.IntValue EPIC_WEIGHT;
	public static final ForgeConfigSpec.IntValue LEGENDARY_WEIGHT;
	public static final ForgeConfigSpec.IntValue MYTHIC_WEIGHT;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.push("General");
		MODIFY_ALL = commonBuilder.comment("If all applicable items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);
		GLOBAL_STRENGTH_MODIFIER = commonBuilder.comment("Changes the base strength of \"most\" modifiers, default 0.2D (20%)").defineInRange("global_strength_modifier", 0.2D, 0.0D, Double.MAX_VALUE);
		EMPTY_AFFIX_CHANCE = commonBuilder.comment("Chance of the item not having a prefix or suffix (seperate)").defineInRange("empty_affix_chance", 0.2D, 0.0D, 1.0D);
		DIM_RARITY_TOOLTIP = commonBuilder.comment("If the rarity should be dimmed.").define("dim_rarity_tooltip", true);

		commonBuilder.pop();

		commonBuilder.comment("Chance of a rarity being chosen out of the others.").push("Rarity Weights");
		COMMON_WEIGHT = commonBuilder.defineInRange("0_common", 11, 0, Integer.MAX_VALUE);
		UNCOMMON_WEIGHT = commonBuilder.defineInRange("1_uncommon", 10, 0, Integer.MAX_VALUE);
		RARE_WEIGHT = commonBuilder.defineInRange("2_rare", 9, 0, Integer.MAX_VALUE);
		EPIC_WEIGHT = commonBuilder.defineInRange("3_epic", 8, 0, Integer.MAX_VALUE);
		LEGENDARY_WEIGHT = commonBuilder.defineInRange("4_legendary", 7, 0, Integer.MAX_VALUE);
		MYTHIC_WEIGHT = commonBuilder.defineInRange("5_mythic", 6, 0, Integer.MAX_VALUE);
		commonBuilder.pop();

		commonBuilder.comment("Registry names of items that the modifier should also be applied to.").push("Item Additions");
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			mod.get().buildAdditionsConfig(commonBuilder);
		}
		commonBuilder.pop();

		commonBuilder.comment("Strength Multiplier of each modifier. (this is also multiplied by global_strength_modifier)").push("Strength Multipliers");
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			mod.get().buildStrengthConfig(commonBuilder);
		}
		commonBuilder.pop();

		commonBuilder.comment("Chance of the prefix being chose out of others.").push("Prefix Weights");
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			if (mod.get().getModifierAffix() == IModifier.Affix.Prefix) {
				mod.get().buildWeightConfig(commonBuilder);
			}
		}
		commonBuilder.pop();

		commonBuilder.comment("Chance of the suffix being chose out of others.").push("Suffix Weights");
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			if (mod.get().getModifierAffix() == IModifier.Affix.Suffix) {
				mod.get().buildWeightConfig(commonBuilder);
			}
		}
		commonBuilder.pop();

		COMMONCONFIG = commonBuilder.build();
	}
}
