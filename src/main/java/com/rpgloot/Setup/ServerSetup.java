package com.rpgloot.Setup;

import com.rpgloot.RPGLoot;
import com.rpgloot.RandomizerTagUpdater;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPGLoot.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ServerSetup {
	public static void init(final FMLClientSetupEvent event) {
		RandomizerTagUpdater.init();
	}

}
