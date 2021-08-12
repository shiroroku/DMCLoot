package com.dmcloot.Item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class EssenceItemColor implements IItemColor {

	@Override
	public int getColor(ItemStack stack, int index) {
		EssenceItem item = (EssenceItem) stack.getItem();
		return item.getRarity().getColor().getColor();
	}

}
