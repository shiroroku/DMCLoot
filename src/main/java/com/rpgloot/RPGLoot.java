package com.rpgloot;

import com.rpgloot.Registry.AttributeRegistry;
import com.rpgloot.Setup.ClientSetup;
import com.rpgloot.Setup.CommonSetup;
import com.rpgloot.Setup.ServerSetup;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RPGLoot.MODID)
public class RPGLoot {

	public static final String MODID = "rpgloot";
	public static final Logger LOGGER = LogManager.getLogger();

	public RPGLoot() {
		Events.init();
		AttributeRegistry.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMONCONFIG);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerSetup::init);
	}
}
