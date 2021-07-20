package com.dmcloot.Modifier.Prefix;

import com.dmcloot.Configuration;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LifestealModifier implements IModifier {

	private static final String modifierName = "dmcloot.lifesteal";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public Affix getModifierAffix() {
		return Affix.Prefix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.LIFESTEAL_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(SwordItem.class, AxeItem.class);
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
			}
		}
	}
}
