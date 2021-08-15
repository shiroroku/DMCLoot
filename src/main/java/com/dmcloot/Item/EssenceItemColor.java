package com.dmcloot.Item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EssenceItemColor implements IItemColor {

	@Override
	public int getColor(ItemStack stack, int index) {
		return ((EssenceItem) stack.getItem()).getRarity().getColor().getColor();
	}

}
