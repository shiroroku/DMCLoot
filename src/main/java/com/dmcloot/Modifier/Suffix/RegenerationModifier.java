package com.dmcloot.Modifier.Suffix;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collections;
import java.util.List;

public class RegenerationModifier implements IModifier {

	private static final String modifierName = "dmcloot.regeneration";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public Affix getModifierAffix() {
		return Affix.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.REGENERATION_ADDITIONS.get();
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
			if (itemStack.getItem() instanceof ShieldItem) {
				return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND };
			}
		}
		return new EquipmentSlotType[] {};
	}

	@Override
	public String getModifierName() {
		return modifierName;
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(RegenerationModifier::onPlayerTick);
	}

	private static void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.player.getHealth() >= e.player.getMaxHealth()) {
			return;
		}
		IModifier modifier = ModifierRegistry.MODIFIERS.REGENERATION.get();
		for (ItemStack armor : e.player.getArmorSlots()) {
			if (modifier.itemHasModifier(armor)) {
				e.player.heal((modifier.getValue(armor) / 100f) * e.player.getMaxHealth() * 0.005f);
			}
		}
	}
}
