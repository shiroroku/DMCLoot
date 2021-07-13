package com.rpgloot.Modifier.Weapon;

import com.rpgloot.Configuration;
import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.AttributeRegistry;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class SpeedModifier implements IModifier {

	private static final String modifierName = "rpgloot.speed";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public ModifierType getModifierType() {
		return ModifierType.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.SPEED_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemTypes() {
		return Arrays.asList(PickaxeItem.class, ShovelItem.class, AxeItem.class);
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
		MinecraftForge.EVENT_BUS.addListener(SpeedModifier::onBreakSpeed);
	}

	private static void onBreakSpeed(PlayerEvent.BreakSpeed e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.SPEED.get();
		ItemStack item = e.getEntityLiving().getMainHandItem();
		if (modifier.itemHasModifier(item)) {
			e.setNewSpeed(e.getOriginalSpeed() + ((Integer) modifier.getValue(item) / 100f) * e.getOriginalSpeed());
		}

	}
}
