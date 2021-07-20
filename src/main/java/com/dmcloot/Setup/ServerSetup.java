package com.dmcloot.Setup;

import com.dmcloot.DMCLoot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DMCLoot.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ServerSetup {
	public static void init(final FMLClientSetupEvent event) {
	}

}
