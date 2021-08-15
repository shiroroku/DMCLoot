package com.dmcloot.Modifier.Prefix;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Modifier.ModifierRarity;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FrostModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.frost";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048.0D));
	private static final RegistryObject<Attribute> ATTRIBUTE2 = AttributeRegistry.ATTRIBUTES.register(modifierName + ".damage", () -> new RangedAttribute("attribute.name." + modifierName + ".damage", 0.0D, 0.0D, 2048.0D));
	private static final Method Blaze_setCharged = ObfuscationReflectionHelper.findMethod(BlazeEntity.class, "func_70844_e", boolean.class);

	public FrostModifier() {
		super(modifierName, Affix.Prefix);
	}

	@Override
	public List<Attribute> getAttribute() {
		return Arrays.asList(ATTRIBUTE.get(), ATTRIBUTE2.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(SwordItem.class, AxeItem.class, BowItem.class, CrossbowItem.class);
	}

	@Override
	public float getMultiplierFromRarity(ModifierRarity rarity) {
		float randomDifference = DMCLoot.randomInstance.nextInt(1) - 2;
		switch (rarity) {
			default:
				return 5f;
			case COMMON:
				return Math.max(10f, 10f + randomDifference);
			case UNCOMMON:
				return 15f + randomDifference;
			case RARE:
				return 20f + randomDifference;
			case EPIC:
				return 30f + randomDifference;
			case LEGENDARY:
				return 40f + randomDifference;
			case MYTHIC:
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
				LivingEntity target = e.getEntityLiving();
				target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, Math.round(mod.getValue(weapon) * 20f), 2));

				if (target.fireImmune() || target.isInWaterOrRain() || target.getClassification(false) == EntityClassification.WATER_CREATURE || target instanceof DrownedEntity || target.level.dimensionType().piglinSafe()) {
					e.setAmount(e.getAmount() + (float) mod.getValue(weapon, ATTRIBUTE2.get()));
					player.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundCategory.PLAYERS, 1f, (float) (8f - player.level.random.nextDouble() * 5f));
				}

				if (target instanceof BlazeEntity) {
					((BlazeEntity) target).setTarget(null);
					try {
						Blaze_setCharged.invoke(target, false);
					} catch (IllegalAccessException | InvocationTargetException exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}
}
