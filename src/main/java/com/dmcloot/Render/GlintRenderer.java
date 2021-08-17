package com.dmcloot.Render;

import com.dmcloot.Configuration.ClientConfiguration;
import com.dmcloot.Util.ModifierHelper;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GlintRenderer {

	private static final ThreadLocal<ItemStack> currentItem = new ThreadLocal<>();

	public static void updateItem(ItemStack itemstack){
		currentItem.set(itemstack);
	}

	public static void getCustomGlint(IRenderTypeBuffer buffer, RenderType type, boolean isEntity, boolean isFoil, boolean isArmor, CallbackInfoReturnable<IVertexBuilder> callback) {
		if (ClientConfiguration.GLINT_EFFECT.get() && !isFoil && !currentItem.get().isEmpty() && ModifierHelper.hasAnyModifier(currentItem.get())) {
			RenderType glintOverride = GlintRenderTypes.getRenderTypeFromModifier(currentItem.get(), isEntity, isArmor);
			if (glintOverride != null) {
				callback.setReturnValue(VertexBuilderUtils.create(buffer.getBuffer(glintOverride), buffer.getBuffer(type)));
			}
		}
	}

}
