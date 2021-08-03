package com.dmcloot.Modifier.Prefix;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FrostModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.frost";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048.0D));
	private static final RegistryObject<Attribute> ATTRIBUTE2 = AttributeRegistry.ATTRIBUTES.register(modifierName + ".damage", () -> new RangedAttribute("attribute.name." + modifierName + ".damage", 0.0D, 0.0D, 2048.0D));

	public FrostModifier() {
		super(modifierName, Affix.Prefix);
	}

	@Override
	public List<Attribute> getAttribute() {
		return Arrays.asList(ATTRIBUTE.get(), ATTRIBUTE2.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(SwordItem.class, AxeItem.class);
	}

	@Override
	public float getMultiplierFromRarity(Rarity rarity) {
		float randomDifference = ModifierRegistry.randomInstance.nextInt(1) - 2;
		switch (rarity) {
			default:
				return 1f;
			case Common:
				return Math.max(2f, 2f + randomDifference);
			case Uncommon:
				return 3f + randomDifference;
			case Rare:
				return 4f + randomDifference;
			case Epic:
				return 6f + randomDifference;
			case Legendary:
				return 8f + randomDifference;
			case Mythic:
				return Math.min(12f, 10f + randomDifference);
		}
	}

	@Override
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a), AttributeModifier.Operation.ADDITION));
	}


	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(FrostModifier::onDamageLiving);
	}

	@Override
	public int getValue(ItemStack item, Attribute a) {
		if (a == ATTRIBUTE2.get()) {
			return getValue(item) / 2;
		} else {
			return getValue(item);
		}
	}

	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier mod = ModifierRegistry.MODIFIERS.FROST.get();
		if (e.getSource().getEntity() == null) {
			return;
		}
		if (e.getSource().getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) e.getSource().getEntity();
			ItemStack weapon = player.getMainHandItem();
			if (mod.itemHasModifier(weapon)) {
				e.getEntityLiving().addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, Math.round(mod.getValue(weapon) * 20f), 2));
				e.setAmount(e.getAmount() + (float) mod.getValue(weapon, ATTRIBUTE2.get()));
			}
		}
	}
}
