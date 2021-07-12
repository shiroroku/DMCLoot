package com.rpgloot.Modifier.Weapon;

import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.AttributeRegistry;
import com.rpgloot.Registry.ModifierRegistry;
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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MendingModifier implements IModifier {

	private static final String modifierName = "rpgloot.mending";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public ModifierType getModifierType() {
		return ModifierType.Prefix;
	}

	@Override
	public List<Class<? extends Item>> getValidItemTypes() {
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
	public Attribute getAttribute() {
		return ATTRIBUTE.get();
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onBlockBreak);
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onDamageLiving);
		MinecraftForge.EVENT_BUS.addListener(MendingModifier::onLivingHurt);

	}

	private static void processModifier(IModifier modifier, Random r, ItemStack stack) {
		if (modifier.itemHasModifier(stack) && stack.isDamageableItem() && stack.getDamageValue() > 0) {
			if (r.nextDouble() < ((Integer) modifier.getValue(stack) / 100f)) {
				stack.setDamageValue(stack.getDamageValue() - 2);
			}
		}
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
