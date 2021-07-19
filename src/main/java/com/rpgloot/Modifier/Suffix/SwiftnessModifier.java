package com.rpgloot.Modifier.Suffix;

import com.rpgloot.Configuration;
import com.rpgloot.Modifier.IModifier;
import com.rpgloot.RPGLoot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SwiftnessModifier implements IModifier {

	private static final String modifierName = "rpgloot.swiftness";

	@Override
	public Affix getModifierAffix() {
		return Affix.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.SWIFTNESS_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(TieredItem.class, ArmorItem.class);
	}

	@Override
	public EquipmentSlotType[] getValidSlotTypes(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem) {
			return new EquipmentSlotType[] { ((ArmorItem) itemStack.getItem()).getSlot() };
		} else {
			return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
		}
	}

	@Override
	public String getModifierName() {
		return modifierName;
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(Attributes.MOVEMENT_SPEED);
	}

	@Override
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((RPGLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (RPGLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}
}