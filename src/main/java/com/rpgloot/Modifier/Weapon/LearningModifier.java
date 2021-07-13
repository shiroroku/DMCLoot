package com.rpgloot.Modifier.Weapon;

import com.rpgloot.Configuration;
import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.AttributeRegistry;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class LearningModifier implements IModifier {

	private static final String modifierName = "rpgloot.learning";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));
	private static final Method getMobExperience = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_70693_a", PlayerEntity.class);

	@Override
	public ModifierType getModifierType() {
		return ModifierType.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.LEARNING_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemTypes() {
		return Arrays.asList(SwordItem.class, AxeItem.class);
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
		MinecraftForge.EVENT_BUS.addListener(LearningModifier::onLivingDeath);
	}

	private static void onLivingDeath(LivingDeathEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.LEARNING.get();
		if (e.getSource().getEntity() == null) {
			return;
		}
		if (e.getSource().getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) e.getSource().getEntity();
			ItemStack weapon = player.getMainHandItem();
			if (modifier.itemHasModifier(weapon)) {
				int xp = 0;
				try {
					xp = (int) getMobExperience.invoke(e.getEntityLiving(), player);
				} catch (IllegalAccessException | InvocationTargetException exception) {
					exception.printStackTrace();
				}
				e.getEntity().level.addFreshEntity(new ExperienceOrbEntity(e.getEntity().level, e.getEntity().getZ(), e.getEntity().getY(), e.getEntity().getZ(), (int) (xp * (Integer) modifier.getValue(weapon) / 100f)));
			}
		}
	}
}
