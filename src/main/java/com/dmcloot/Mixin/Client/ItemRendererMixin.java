package com.dmcloot.Mixin.Client;

import com.dmcloot.Configuration.ClientConfiguration;
import com.dmcloot.Render.RenderTypes;
import com.dmcloot.Util.ModifierHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	private static final ThreadLocal<ItemStack> currentItem = new ThreadLocal<>();

	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V", cancellable = true)
	private void render(ItemStack stack, ItemCameraTransforms.TransformType transform, boolean leftHand, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, IBakedModel model, CallbackInfo callback) {
		currentItem.set(stack);
	}

	@Inject(at = @At("HEAD"), method = "getFoilBufferDirect(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;", cancellable = true)
	private static void getFoilBufferDirect(IRenderTypeBuffer buffer, RenderType type, boolean bool, boolean isfoil, CallbackInfoReturnable<IVertexBuilder> callback) {
		if (ClientConfiguration.GLINT_EFFECT.get() && !isfoil && !currentItem.get().isEmpty() && ModifierHelper.hasAnyModifier(currentItem.get())) {
			RenderType glintOverride = RenderTypes.getRenderTypeFromModifier(currentItem.get());
			if (glintOverride != null) {
				callback.setReturnValue(VertexBuilderUtils.create(buffer.getBuffer(glintOverride), buffer.getBuffer(type)));
			}
		}
	}

}
