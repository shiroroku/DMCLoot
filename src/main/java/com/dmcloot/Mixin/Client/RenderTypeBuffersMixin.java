package com.dmcloot.Mixin.Client;

import com.dmcloot.Configuration.ClientConfiguration;
import com.dmcloot.Render.RenderTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTypeBuffers.class)
public class RenderTypeBuffersMixin {

	@Inject(at = @At("HEAD"), method = "put(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;Lnet/minecraft/client/renderer/RenderType;)V", cancellable = true)
	private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, RenderType type, CallbackInfo ci) {
		if (ClientConfiguration.GLINT_EFFECT.get()) {
			RenderTypes.mixinFixedBuffers(map);
		}
	}
}
