package com.dmcloot;

import com.dmcloot.Modifier.ModifierBase;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;

public class CuriosCompat {

	public static void handleItemAttribute(ItemAttributeModifierEvent e, ModifierBase mod) {
		for (Attribute a : mod.getAttribute()) {
			if (!e.getModifiers().containsKey(a)) {
				if (mod.canApply(e.getItemStack())) {
					mod.applyItemAttibute(a, e);
				}
			}
		}
	}

	public static boolean isCurio(ItemStack item) {
		return item.getItem().getTags().stream().filter(rs -> rs.getNamespace().equals("curios")).findFirst().orElse(null) != null;
	}

}
