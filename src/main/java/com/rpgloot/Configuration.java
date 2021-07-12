package com.rpgloot;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

	public static ForgeConfigSpec COMMONCONFIG;

	public static ForgeConfigSpec.BooleanValue MODIFY_ALL;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.comment("Default strength muliplier of each rarity.").push("Default Rarity Multipliers");
		MODIFY_ALL = commonBuilder.comment("If all items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);

		commonBuilder.pop();

		COMMONCONFIG = commonBuilder.build();
	}
}
