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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class FrostModifier implements IModifier {

	private static final String modifierName = "rpgloot.frost";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048.0D));

	@Override
	public ModifierType getModifierType() {
		return ModifierType.Prefix;
	}

	@Override
	public List<Class<? extends Item>> getValidItemTypes() {
		return Arrays.asList(SwordItem.class, AxeItem.class);
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.FROST_ADDITIONS.get();
	}

	@Override
	public float getMultiplierFromRarity(ModifierRarity rarity) {
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
			case Ledgendary:
				return 8f + randomDifference;
			case Mythic:
				return Math.min(12f, 10f + randomDifference);
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
		MinecraftForge.EVENT_BUS.addListener(FrostModifier::onDamageLiving);
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
				e.getEntityLiving().addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, Math.round((int) mod.getValue(weapon) * 20f), 2));
			}
		}
	}
}
