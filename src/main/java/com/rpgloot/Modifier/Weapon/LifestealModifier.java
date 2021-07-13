package com.rpgloot.Modifier.Weapon;

import com.rpgloot.Configuration;
import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.AttributeRegistry;
import com.rpgloot.Registry.ModifierRegistry;
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
import java.util.List;

public class LifestealModifier implements IModifier {

	private static final String modifierName = "rpgloot.lifesteal";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public ModifierType getModifierType() {
		return ModifierType.Prefix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.LIFESTEAL_ADDITIONS.get();
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
				player.heal(((Integer) modifier.getValue(weapon) / 100f) * e.getAmount());
			}
		}
	}
}
