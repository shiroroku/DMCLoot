package com.dmcloot;

import com.dmcloot.Configuration.ClientConfiguration;
import com.dmcloot.Configuration.CommonConfiguration;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ItemRegistry;
import com.dmcloot.Util.ItemModifierAutoRandomizer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(DMCLoot.MODID)
public class DMCLoot {

	public static final String MODID = "dmcloot";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Random randomInstance = new Random();

	public DMCLoot() {
		ItemRegistry.init();
		Events.init();
		AttributeRegistry.init();
		ItemModifierAutoRandomizer.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.COMMONCONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfiguration.CLIENTCONFIG);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
	}
}
