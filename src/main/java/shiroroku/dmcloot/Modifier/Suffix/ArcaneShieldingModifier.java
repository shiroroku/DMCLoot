package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArcaneShieldingModifier extends ModifierBase {

	private static final String modifierName = "dmcloot.arcaneshielding";
	private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

	public ArcaneShieldingModifier() {
		super(modifierName, Affix.Suffix, new Color(66, 75, 255));
	}

	@Override
	public List<Attribute> getAttribute() {
		return Collections.singletonList(ATTRIBUTE.get());
	}

	@Override
	public List<Class<? extends Item>> getValidItemClasses() {
		return Arrays.asList(ArmorItem.class, ShieldItem.class);
	}

	@Override
	public EquipmentSlot[] getValidSlotTypes(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem) {
			return new EquipmentSlot[] { ((ArmorItem) itemStack.getItem()).getSlot() };
		}
		return new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND };
	}

	@Override
	public void handleEventRegistry() {
		MinecraftForge.EVENT_BUS.addListener(ArcaneShieldingModifier::onDamageLiving);
	}

	private static void onDamageLiving(LivingDamageEvent e) {
		IModifier mod = ModifierRegistry.MODIFIERS.ARCANESHIELDING.get();
		if (e.getSource().isMagic()) {
			LivingEntity attacked = e.getEntity();
			float damageReduction = 0f;

			ItemStack mainHand = attacked.getMainHandItem();
			if (mod.itemHasModifier(mainHand)) {
				damageReduction += (mod.getValue(mainHand) / 100f);
			}

			ItemStack offHand = attacked.getOffhandItem();
			if (mod.itemHasModifier(offHand)) {
				damageReduction += (mod.getValue(offHand) / 100f);
			}

			for (ItemStack armor : attacked.getArmorSlots()) {
				if (mod.itemHasModifier(armor)) {
					damageReduction += (mod.getValue(armor) / 100f);
				}
			}

			damageReduction = Math.min(damageReduction, 1f);
			e.setAmount(e.getAmount() * (1f - damageReduction));
		}
	}
}
