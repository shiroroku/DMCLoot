package com.dmcloot.Modifier.Prefix;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MendingModifier implements IModifier {

	private static final String modifierName = "dmcloot.mending";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public Affix getModifierAffix() {
		return Affix.Prefix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.MENDING_ADDITIONS.get();
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
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onBlockBreak);
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onDamageLiving);
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onLivingHurt);
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onUseHoeEvent);

	}

	private static void processModifier(IModifier modifier, Random r, ItemStack stack) {
		if (modifier.itemHasModifier(stack) && stack.isDamageableItem() && stack.getDamageValue() > 0) {
			if (r.nextDouble() < (modifier.getValue(stack) / 100f)) {
				stack.setDamageValue(stack.getDamageValue() - 2);
			}
		}
	}

	/**
	 * When the player uses a hoe.
	 */
	private static void onUseHoeEvent(UseHoeEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
		processModifier(modifier, e.getPlayer().getRandom(), e.getPlayer().getMainHandItem());
	}

	/**
	 * When the player breaks a block, heal mainhand
	 */
	private static void onBlockBreak(BlockEvent.BreakEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
		processModifier(modifier, e.getPlayer().getRandom(), e.getPlayer().getMainHandItem());
	}

	/**
	 * When the player gets hurt, heal armor
	 */
	private static void onLivingHurt(LivingHurtEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
		Iterable<ItemStack> items = e.getEntityLiving().getArmorSlots();
		for (ItemStack stack : items) {
			processModifier(modifier, e.getEntityLiving().getRandom(), stack);
		}
	}

	/**
	 * When the player attacks, heal mainhand
	 */
	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
		if (e.getSource().getEntity() == null) {
			return;
		}
		if (e.getSource().getEntity() instanceof PlayerEntity) {
			processModifier(modifier, e.getEntityLiving().getRandom(), ((PlayerEntity) e.getSource().getEntity()).getMainHandItem());
		}
	}
}
