package com.dmcloot;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Configuration {

	public static ForgeConfigSpec COMMONCONFIG;

	public static ForgeConfigSpec.BooleanValue MODIFY_ALL;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.push("General");
		MODIFY_ALL = commonBuilder.comment("If all items should recive random modifiers. Else they must be randomized through nbt.").define("modify_all", true);
		commonBuilder.comment("Registry names of items that the modifier should also be applied to.").push("Item Additions");
		for (ModifierRegistry.MODIFIERS mod : ModifierRegistry.MODIFIERS.values()) {
			mod.get().buildAdditionsConfig(commonBuilder);
		}
		commonBuilder.pop();
		commonBuilder.pop();

		//commonBuilder.push("Modifier Weights");
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
		//commonBuilder.pop();

		COMMONCONFIG = commonBuilder.build();
	}
}
