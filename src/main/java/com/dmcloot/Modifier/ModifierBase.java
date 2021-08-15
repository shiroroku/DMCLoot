package com.dmcloot.Modifier;

import com.dmcloot.Configuration.CommonConfiguration;
import com.dmcloot.Compat.CuriosCompat;
import com.dmcloot.DMCLoot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ModifierBase implements IModifier {

	private ForgeConfigSpec.ConfigValue<List<String>> ADDITIONS = null;
	private ForgeConfigSpec.IntValue WEIGHT = null;
	private ForgeConfigSpec.DoubleValue STRENGTH = null;

	private final String name;
	private final Affix affix;

	public ModifierBase(String name, Affix affix) {
		this.name = name;
		this.affix = affix;
	}

	@Override
	public String getModifierName() {
		return name;
	}

	@Override
	public Affix getModifierAffix() {
		return affix;
	}

	public void buildAdditionsConfig(ForgeConfigSpec.Builder builder) {
		ADDITIONS = builder.define(this.getModifierName().split("\\.")[1], new ArrayList<>());
	}

	public void buildStrengthConfig(ForgeConfigSpec.Builder builder) {
		STRENGTH = builder.defineInRange(this.getModifierName().split("\\.")[1], 1, 0, Double.MAX_VALUE);
	}

	public void buildWeightConfig(ForgeConfigSpec.Builder builder) {
		WEIGHT = builder.defineInRange(this.getModifierName().split("\\.")[1], 5, 0, Integer.MAX_VALUE);
	}

	public int getWeight() {
		return WEIGHT.get();
	}

	public List<String> getAdditions() {
		return ADDITIONS.get();
	}

	public double getStrengthMultiplier() {
		return STRENGTH.get();
	}

	/**
	 * Applies this modifier and changes value depending on rarity.
	 */
	public void applyWithRarity(ItemStack stack, ModifierRarity rarity) {
		float globalStrength = CommonConfiguration.GLOBAL_STRENGTH_MODIFIER.get().floatValue();
		apply(stack, (int) (getMultiplierFromRarity(rarity) * globalStrength * getStrengthMultiplier()));
	}

	/**
	 * Applies this modifier and NBT value to the given ItemStack.
	 */
	public void apply(ItemStack stack, int value) {
		if (canApply(stack)) {
			stack.getOrCreateTag().putInt(getModifierName(), value);
		}
	}

	/**
	 * Returns if the stack can support this modifier. Use getValidItemClasses to specify classes.
	 */
	public boolean canApply(ItemStack stack) {
		List<String> additions = getAdditions();
		List<Class<? extends Item>> itemtypes = getValidItemClasses();
		boolean add = itemtypes == null || itemtypes.size() == 0;
		if (!add) {
			for (Class<? extends Item> type : itemtypes) {
				if (type.isAssignableFrom(stack.getItem().getClass())) {
					add = true;
				}
			}
		}
		for (String id : additions) {
			if (!id.isEmpty()) {
				if (id.startsWith("#") && stack.getItem().getTags().contains(ResourceLocation.tryParse(id.replace("#", "")))) {
					add = true;
				} else {
					Item fromreg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
					if (fromreg != null && fromreg.getItem() == stack.getItem()) {
						add = true;
					}
				}
			}
		}
		return add;
	}

	/**
	 * Handles what items the modifier's Attribute should be applied to.
	 */
	public void handleItemAttribute(ItemAttributeModifierEvent e) {
		if (CuriosCompat.isCurio(e.getItemStack())) {
			//CuriosCompat.handleItemAttribute(e, this);
			return;
		}
		for (EquipmentSlotType slot : getValidSlotTypes(e.getItemStack())) {
			if (e.getSlotType() == slot) {
				for (Attribute a : getAttribute()) {
					if (!e.getModifiers().containsKey(a)) {
						if (canApply(e.getItemStack())) {
							applyItemAttibute(a, e);
						}
					}
				}
			}
		}
	}

	/**
	 * Applies Attribute to items that have this modifier.
	 */
	public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_BASE));
	}

}
