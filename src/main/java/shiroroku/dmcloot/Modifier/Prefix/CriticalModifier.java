package shiroroku.dmcloot.Modifier.Prefix;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CriticalModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.critical";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public CriticalModifier() {
		super(modifierName, Affix.Prefix, new Color(170, 255, 66));
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
		ItemStack stack = e.getEntity().getMainHandItem();
		if (modifier.itemHasModifier(stack)) {
			if (e.getEntity().getRandom().nextDouble() < (modifier.getValue(stack) / 100f)) {
				e.setResult(Event.Result.ALLOW);
			}
		}
	}
}
