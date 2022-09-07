package com.dmcloot.Modifier.Prefix;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierBase;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AquaticModifier extends ModifierBase {

	public AquaticModifier() {
		super("dmcloot.aquatic", Affix.Prefix, new Color(66, 110, 255));
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ForgeMod.SWIM_SPEED.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Collections.singletonList(ArmorItem.class);
	}

	@Override
	public EquipmentSlotType[] getValidSlotTypes(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem) {
			return new EquipmentSlotType[] { ((ArmorItem) itemStack.getItem()).getSlot() };
		} else {
			return new EquipmentSlotType[] {};
		}
	}

	@Override
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}
}
