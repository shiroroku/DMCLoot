package com.dmcloot.Mixin.Client;

import com.dmcloot.Render.GlintRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BipedArmorLayer.class)
public class BipedArmorLayerMixin<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> {

	@Inject(at = @At("HEAD"), method = "getArmorModelHook(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/client/renderer/entity/model/BipedModel;)Lnet/minecraft/client/renderer/entity/model/BipedModel;", remap = false)
	private void getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model, CallbackInfoReturnable<A> cir) {
		GlintRenderer.updateItem(itemStack);
	}

}
