package com.dmcloot.Registry;

import com.dmcloot.DMCLoot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeRegistry {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, DMCLoot.MODID);

	public static void init() {
		ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
