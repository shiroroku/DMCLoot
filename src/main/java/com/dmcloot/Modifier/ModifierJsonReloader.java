package com.dmcloot.Modifier;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class ModifierJsonReloader extends JsonReloadListener {

	public ModifierJsonReloader() {
		super((new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create(), "modifiers");
	}

	@Override
	public void apply(Map<ResourceLocation, JsonElement> map, IResourceManager manager, IProfiler profiler) {
		
	}

}
