package com.dmcloot.Modifier.Prefix;

import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.AttributeRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CriticalModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.critical";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public CriticalModifier() {
		super(modifierName, Affix.Prefix);
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(SwordItem.class, AxeItem.class);
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(CriticalModifier::onCriticalHitEvent);
	}

	private static void onCriticalHitEvent(CriticalHitEvent e) {
		IModifier modifier = ModifierRegistry.MODIFIERS.CRITICAL.get();
		ItemStack stack = e.getPlayer().getMainHandItem();
		if (modifier.itemHasModifier(stack)) {
			if (e.getPlayer().getRandom().nextDouble() < (modifier.getValue(stack) / 100f)) {
				e.setResult(Event.Result.ALLOW);
			}
		}
	}
}
