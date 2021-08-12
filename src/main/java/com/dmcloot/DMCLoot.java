package com.dmcloot;

import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ItemRegistry;
import com.dmcloot.Setup.ClientSetup;
import com.dmcloot.Setup.CommonSetup;
import com.dmcloot.Setup.ServerSetup;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DMCLoot.MODID)
public class DMCLoot {

	public static final String MODID = "dmcloot";
	public static final Logger LOGGER = LogManager.getLogger();

	public DMCLoot() {
		ItemRegistry.init();
		Events.init();
		AttributeRegistry.init();
		RandomizerTagUpdater.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMONCONFIG);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerSetup::init);
	}
}
