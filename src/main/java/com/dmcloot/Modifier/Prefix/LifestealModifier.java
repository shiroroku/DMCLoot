package com.dmcloot.Modifier.Prefix;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LifestealModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.lifesteal";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public LifestealModifier() {
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
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(LifestealModifier::onDamageLiving);
	}

	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.LIFESTEAL.get();
		if (e.getSource().getEntity() == null) {
			return;
		}
		if (e.getSource().getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) e.getSource().getEntity();
			ItemStack weapon = player.getMainHandItem();
			if (modifier.itemHasModifier(weapon)) {
				player.heal((modifier.getValue(weapon) / 100f) * e.getAmount());
				player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITCH_DRINK, SoundCategory.PLAYERS, 0.5f, 1f);
			}
		}
	}
}
