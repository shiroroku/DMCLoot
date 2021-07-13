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
	public static ForgeConfigSpec.ConfigValue<List<String>> LEARNING_ADDITIONS;
	public static ForgeConfigSpec.ConfigValue<List<String>> LIFESTEAL_ADDITIONS;
	public static ForgeConfigSpec.ConfigValue<List<String>> MENDING_ADDITIONS;
	public static ForgeConfigSpec.ConfigValue<List<String>> SPEED_ADDITIONS;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.comment("Default strength muliplier of each rarity.").push("Default Rarity Multipliers");
		MODIFY_ALL = commonBuilder.comment("If all items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);

		commonBuilder.comment("Registry names of items that the modifier should also be applied to.").push("Additions");
		FROST_ADDITIONS = commonBuilder.define("additions_frost", new ArrayList<>());
		LEARNING_ADDITIONS = commonBuilder.define("additions_learning", new ArrayList<>());
		LIFESTEAL_ADDITIONS = commonBuilder.define("additions_lifesteal", new ArrayList<>());
		MENDING_ADDITIONS = commonBuilder.define("additions_mending", new ArrayList<>());
		SPEED_ADDITIONS = commonBuilder.define("additions_speed", new ArrayList<>());
		commonBuilder.pop();

		commonBuilder.pop();
		COMMONCONFIG = commonBuilder.build();
	}
}
