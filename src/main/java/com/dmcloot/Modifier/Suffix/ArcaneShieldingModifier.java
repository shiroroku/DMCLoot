package com.dmcloot.Modifier.Suffix;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArcaneShieldingModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.arcaneshielding";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public ArcaneShieldingModifier() {
		super(modifierName, Affix.Suffix, new Color(66, 75, 255));
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
		MinecraftForge.EVENT_BUS.addListener(ArcaneShieldingModifier::onDamageLiving);
	}

	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier mod = ModifierRegistry.MODIFIERS.ARCANESHIELDING.get();
		if (e.getSource().isMagic()) {
			LivingEntity attacked = e.getEntityLiving();
			float damageReduction = 0f;

			ItemStack mainHand = attacked.getMainHandItem();
			if (mod.itemHasModifier(mainHand)) {
				damageReduction += (mod.getValue(mainHand) / 100f);
			}

			ItemStack offHand = attacked.getOffhandItem();
			if (mod.itemHasModifier(offHand)) {
				damageReduction += (mod.getValue(offHand) / 100f);
			}

			for (ItemStack armor : attacked.getArmorSlots()) {
				if (mod.itemHasModifier(armor)) {
					damageReduction += (mod.getValue(armor) / 100f);
				}
			}

			damageReduction = Math.min(damageReduction, 1f);
			e.setAmount(e.getAmount() * (1f - damageReduction));
		}
	}
}
