package com.dmcloot.Modifier.Prefix;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LightningModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.lightning";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName + ".damage", () -> new RangedAttribute("attribute.name." + modifierName + ".damage", 0.0D, 0.0D, 2048.0D));

	public LightningModifier() {
		super(modifierName, Affix.Prefix);
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(SwordItem.class, AxeItem.class, BowItem.class, CrossbowItem.class);
	}

	@Override
	public float getMultiplierFromRarity(Rarity rarity) {
		float randomDifference = ModifierRegistry.randomInstance.nextInt(1) - 2;
		switch (rarity) {
			default:
				return 5f;
			case Common:
				return Math.max(10f, 10f + randomDifference);
			case Uncommon:
				return 15f + randomDifference;
			case Rare:
				return 20f + randomDifference;
			case Epic:
				return 30f + randomDifference;
			case Legendary:
				return 40f + randomDifference;
			case Mythic:
				return Math.min(60f, 50f + randomDifference);
		}
	}

	@Override
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a), AttributeModifier.Operation.ADDITION));
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(LightningModifier::onDamageLiving);
	}

	@Override
	public int getValue(ItemStack item, Attribute a) {
		return getValue(item) / 2;
	}

	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier mod = ModifierRegistry.MODIFIERS.LIGHTNING.get();
		if (e.getSource().getEntity() == null) {
			return;
		}
		if (e.getSource().getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) e.getSource().getEntity();
			ItemStack weapon = player.getMainHandItem();
			if (mod.itemHasModifier(weapon)) {
				LivingEntity target = e.getEntityLiving();
				if (target instanceof IronGolemEntity || target instanceof EnderDragonEntity || (target.isInWaterOrRain() && target.getClassification(false) != EntityClassification.WATER_CREATURE) || target.getArmorCoverPercentage() > 0.0f) {
					e.setAmount(e.getAmount() + (float) mod.getValue(weapon));
					player.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 0.70f, (float) (8f - player.level.random.nextDouble() * 5f));
				}
			}
		}
	}
}
