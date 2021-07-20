package com.dmcloot.Modifier.Suffix;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.DMCLoot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TieredItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuardingModifier implements IModifier {

	private static final String modifierName = "dmcloot.guarding";

	@Override
	public Affix getModifierAffix() {
		return Affix.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.GUARDING_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(TieredItem.class, ShieldItem.class);
	}

	@Override
	public EquipmentSlotType[] getValidSlotTypes(ItemStack itemStack) {
		return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND };
	}

	@Override
	public String getModifierName() {
		return modifierName;
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(Attributes.ARMOR);
	}

	@Override
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}
}
