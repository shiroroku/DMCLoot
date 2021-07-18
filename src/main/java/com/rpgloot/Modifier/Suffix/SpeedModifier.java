package com.rpgloot.Modifier.Suffix;

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
import java.util.Collections;
import java.util.List;

public class SpeedModifier implements IModifier {

	private static final String modifierName = "rpgloot.speed";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 100.0D));

	@Override
	public Affix getModifierAffix() {
		return Affix.Suffix;
	}

	@Override
	public List<String> getAdditions() {
		return Configuration.SPEED_ADDITIONS.get();
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(PickaxeItem.class, ShovelItem.class, AxeItem.class);
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
		MinecraftForge.EVENT_BUS.addListener(SpeedModifier::onBreakSpeed);
	}

	private static void onBreakSpeed(PlayerEvent.BreakSpeed e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.SPEED.get();
		ItemStack item = e.getEntityLiving().getMainHandItem();
		if (modifier.itemHasModifier(item)) {
			e.setNewSpeed(e.getOriginalSpeed() + (modifier.getValue(item) / 100f) * e.getOriginalSpeed());
		}

	}
}
