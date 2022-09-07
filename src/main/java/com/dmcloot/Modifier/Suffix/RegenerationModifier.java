package com.dmcloot.Modifier.Suffix;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
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

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegenerationModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.regeneration";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public RegenerationModifier() {
		super(modifierName, Affix.Suffix, new Color(255, 66, 145));
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(ArmorItem.class, ShieldItem.class);
	}

	@Override
	public EquipmentSlotType[] getValidSlotTypes(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem) {
			return new EquipmentSlotType[] { ((ArmorItem) itemStack.getItem()).getSlot() };
		}
		return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND };
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
		float baseHealing = 0.005f;

		ItemStack mainHand = e.player.getMainHandItem();
		if (modifier.itemHasModifier(mainHand)) {
			e.player.heal((modifier.getValue(mainHand) / 100f) * e.player.getMaxHealth() * baseHealing);
		}

		ItemStack offHand = e.player.getOffhandItem();
		if (modifier.itemHasModifier(offHand)) {
			e.player.heal((modifier.getValue(offHand) / 100f) * e.player.getMaxHealth() * baseHealing);
		}

		for (ItemStack armor : e.player.getArmorSlots()) {
			if (modifier.itemHasModifier(armor)) {
				e.player.heal((modifier.getValue(armor) / 100f) * e.player.getMaxHealth() * baseHealing);
			}
		}
	}
}
