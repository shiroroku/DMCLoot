package com.dmcloot;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Configuration {

	public static ForgeConfigSpec COMMONCONFIG;

	public static ForgeConfigSpec.BooleanValue MODIFY_ALL;

	public static ForgeConfigSpec.ConfigValue<List<String>> FROST_ADDITIONS;
	public static ForgeConfigSpec.IntValue FROST_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> FIRE_ADDITIONS;
	public static ForgeConfigSpec.IntValue FIRE_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> LEARNING_ADDITIONS;
	public static ForgeConfigSpec.IntValue LEARNING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> LIFESTEAL_ADDITIONS;
	public static ForgeConfigSpec.IntValue LIFESTEAL_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> MENDING_ADDITIONS;
	public static ForgeConfigSpec.IntValue MENDING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> SPEED_ADDITIONS;
	public static ForgeConfigSpec.IntValue SPEED_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> SWIFTNESS_ADDITIONS;
	public static ForgeConfigSpec.IntValue SWIFTNESS_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> GUARDING_ADDITIONS;
	public static ForgeConfigSpec.IntValue GUARDING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> REACHING_ADDITIONS;
	public static ForgeConfigSpec.IntValue REACHING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> REGENERATION_ADDITIONS;
	public static ForgeConfigSpec.IntValue REGENERATION_WEIGHT;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.comment("Default strength muliplier of each rarity.").push("Default Rarity Multipliers");
		MODIFY_ALL = commonBuilder.comment("If all items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);
		commonBuilder.comment("Registry names of items that the modifier should also be applied to.").push("Item Additions");
		FROST_ADDITIONS = commonBuilder.define("additions_frost", new ArrayList<>());
		FIRE_ADDITIONS = commonBuilder.define("additions_fire", new ArrayList<>());
		LEARNING_ADDITIONS = commonBuilder.define("additions_learning", new ArrayList<>());
		LIFESTEAL_ADDITIONS = commonBuilder.define("additions_lifesteal", new ArrayList<>());
		MENDING_ADDITIONS = commonBuilder.define("additions_mending", new ArrayList<>());
		SPEED_ADDITIONS = commonBuilder.define("additions_speed", new ArrayList<>());
		SWIFTNESS_ADDITIONS = commonBuilder.define("additions_swiftness", new ArrayList<>());
		GUARDING_ADDITIONS = commonBuilder.define("additions_guarding", new ArrayList<>());
		REACHING_ADDITIONS = commonBuilder.define("additions_reaching", new ArrayList<>());
		REGENERATION_ADDITIONS = commonBuilder.define("additions_regenearation", new ArrayList<>());
		commonBuilder.pop();
		commonBuilder.pop();

		commonBuilder.push("Modifier Weights");
		commonBuilder.comment("Chance of the prefix being chose out of others.").push("Prefix Weights");
		FROST_WEIGHT = commonBuilder.defineInRange("weight_frost", 5, 0, Integer.MAX_VALUE);
		FIRE_WEIGHT = commonBuilder.defineInRange("weight_fire", 5, 0, Integer.MAX_VALUE);
		LIFESTEAL_WEIGHT = commonBuilder.defineInRange("weight_lifesteal", 5, 0, Integer.MAX_VALUE);
		MENDING_WEIGHT = commonBuilder.defineInRange("weight_mending", 5, 0, Integer.MAX_VALUE);
		REACHING_WEIGHT = commonBuilder.defineInRange("weight_reaching", 5, 0, Integer.MAX_VALUE);
		commonBuilder.pop();
		commonBuilder.comment("Chance of the suffix being chose out of others.").push("Suffix Weights");
		LEARNING_WEIGHT = commonBuilder.defineInRange("weight_learning", 5, 0, Integer.MAX_VALUE);
		SPEED_WEIGHT = commonBuilder.defineInRange("weight_speed", 5, 0, Integer.MAX_VALUE);
		SWIFTNESS_WEIGHT = commonBuilder.defineInRange("weight_swiftness", 5, 0, Integer.MAX_VALUE);
		GUARDING_WEIGHT = commonBuilder.defineInRange("weight_guarding", 5, 0, Integer.MAX_VALUE);
		REGENERATION_WEIGHT = commonBuilder.defineInRange("weight_regeneration", 5, 0, Integer.MAX_VALUE);
		commonBuilder.pop();
		commonBuilder.pop();

		COMMONCONFIG = commonBuilder.build();
	}
}
