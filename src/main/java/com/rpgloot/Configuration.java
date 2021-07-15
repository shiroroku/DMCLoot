package com.rpgloot;

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
	public static ForgeConfigSpec.ConfigValue<List<String>> LEARNING_ADDITIONS;
	public static ForgeConfigSpec.IntValue LEARNING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> LIFESTEAL_ADDITIONS;
	public static ForgeConfigSpec.IntValue LIFESTEAL_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> MENDING_ADDITIONS;
	public static ForgeConfigSpec.IntValue MENDING_WEIGHT;
	public static ForgeConfigSpec.ConfigValue<List<String>> SPEED_ADDITIONS;
	public static ForgeConfigSpec.IntValue SPEED_WEIGHT;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.push("Default Rarity Multipliers").comment("Default strength muliplier of each rarity.");
		MODIFY_ALL = commonBuilder.comment("If all items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);
		commonBuilder.push("Item Additions").comment("Registry names of items that the modifier should also be applied to.");
		FROST_ADDITIONS = commonBuilder.define("additions_frost", new ArrayList<>());
		LEARNING_ADDITIONS = commonBuilder.define("additions_learning", new ArrayList<>());
		LIFESTEAL_ADDITIONS = commonBuilder.define("additions_lifesteal", new ArrayList<>());
		MENDING_ADDITIONS = commonBuilder.define("additions_mending", new ArrayList<>());
		SPEED_ADDITIONS = commonBuilder.define("additions_speed", new ArrayList<>());
		commonBuilder.pop();
		commonBuilder.pop();

		commonBuilder.push("Modifier Weights");
		commonBuilder.push("Prefix Weights").comment("Chance of the prefix being chose out of others.");
		FROST_WEIGHT = commonBuilder.defineInRange("weight_frost", 5, 0, Integer.MAX_VALUE);
		LIFESTEAL_WEIGHT = commonBuilder.defineInRange("weight_lifesteal", 5, 0, Integer.MAX_VALUE);
		MENDING_WEIGHT = commonBuilder.defineInRange("weight_mending", 5, 0, Integer.MAX_VALUE);
		commonBuilder.pop();
		commonBuilder.push("Suffix Weights").comment("Chance of the suffix being chose out of others.");
		LEARNING_WEIGHT = commonBuilder.defineInRange("weight_learning", 5, 0, Integer.MAX_VALUE);
		SPEED_WEIGHT = commonBuilder.defineInRange("weight_weight", 5, 0, Integer.MAX_VALUE);
		commonBuilder.pop();
		commonBuilder.pop();

		COMMONCONFIG = commonBuilder.build();
	}
}
