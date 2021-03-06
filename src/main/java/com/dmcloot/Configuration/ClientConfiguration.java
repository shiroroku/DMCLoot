package com.dmcloot.Configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientConfiguration {

	public static final ForgeConfigSpec CLIENTCONFIG;

	public static final ForgeConfigSpec.BooleanValue GLINT_EFFECT;

	static {
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		clientBuilder.push("General");
		GLINT_EFFECT = clientBuilder.comment("If Glint effects on fire/frost/etc items should show.").define("glint_effect", true);
		clientBuilder.pop();

		CLIENTCONFIG = clientBuilder.build();
	}
}
