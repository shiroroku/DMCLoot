package com.dmcloot.Modifier;

import com.dmcloot.CuriosCompat;
import com.dmcloot.DMCLoot;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IModifier {

	enum Affix {
		Prefix,
		Suffix
	}

	enum Rarity {
		Common(TextFormatting.WHITE, "common"),
		Uncommon(TextFormatting.GREEN, "uncommon"),
		Rare(TextFormatting.BLUE, "rare"),
		Epic(TextFormatting.DARK_PURPLE, "epic"),
		Legendary(TextFormatting.GOLD, "legendary"),
		Mythic(TextFormatting.RED, "mythic");

		private final TextFormatting color;
		private final String name;

		Rarity(TextFormatting color, String name) {
			this.color = color;
			this.name = name;
		}

		public TextFormatting getColor() {
			return this.color;
		}

		public String toString() {
			return this.name;
		}
	}

	/**
	 * Returns whether the modifier is a prefix or a suffix.
	 */
	Affix getModifierAffix();

	/**
	 * Returns the suffix/prefix translation component of this modifier.
	 */
	default TranslationTextComponent getTranslationComponent() {
		return new TranslationTextComponent("modifier.name." + getModifierName());
	}

	/**
	 * Returns the multiplier for each rarity. Default is 1 - 100 for percentages.
	 * Can be used as values for each rarity if getDefaultValue is 1.
	 */
	default float getMultiplierFromRarity(Rarity rarity) {
		float randomDifference = ModifierRegistry.randomInstance.nextInt(6) - 5;
		float globalStrength = 0.2f;
		switch (rarity) {
			default:
				return 1f;
			case Common:
				return Math.max(5f, (5f + randomDifference) * globalStrength);
			case Uncommon:
				return (10f + randomDifference) * globalStrength;
			case Rare:
				return (20f + randomDifference) * globalStrength;
			case Epic:
				return (40f + randomDifference) * globalStrength;
			case Legendary:
				return (70f + randomDifference) * globalStrength;
			case Mythic:
				return Math.min(100f, (100f + randomDifference) * globalStrength);
		}
	}

	/**
	 * The name of the modifier, must be NBT friendly as it's used to indicate this modifiers presence.
	 */
	String getModifierName();

	/**
	 * Points to the registered Attribute(s) for this modifier.
	 */
	List<Attribute> getAttribute();

	/**
	 * Slot(s) that this modifier is active in.
	 */
	default EquipmentSlotType[] getValidSlotTypes(ItemStack item) {
		return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
	}

	/**
	 * Gets avaliable item classes that this modifier can be applied to. Return null for all items.
	 */
	default List<Class<? extends Item>> getValidItemClasses() {
		return null;
	}

	/**
	 * The default strength value of this modifier.
	 */
	default int getDefaultValue() {
		return 1;
	}

	/**
	 * List of registry names this modifier should also apply to.
	 */
	default List<String> getAdditions() {
		return new ArrayList<>();
	}

	/**
	 * Handles what items the modifier's Attribute should be applied to.
	 */
	default void handleItemAttribute(ItemAttributeModifierEvent e) {
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
	default void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
		UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
		e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_BASE));
	}

	/**
	 * Returns if the stack can support this modifier. Use getValidItemClasses to specify classes.
	 */
	default boolean canApply(ItemStack stack) {
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
	 * Applies this modifier and NBT value to the given ItemStack. Object is integer by default.
	 */
	default void apply(ItemStack stack, Object value) {
		if (canApply(stack)) {
			stack.getOrCreateTag().putInt(getModifierName(), (Integer) value);
		}
	}

	/**
	 * Applies this modifier and changes value depending on rarity.
	 */
	default void applyWithRarity(ItemStack stack, Rarity rarity) {
		apply(stack, (int) (getDefaultValue() * getMultiplierFromRarity(rarity)));
	}

	/**
	 * Returns the saved NBT value of this modifier.
	 *
	 * @return 0 if this item doesnt have this modifier.
	 */
	default int getValue(ItemStack stack) {
		if (itemHasModifier(stack)) {
			return stack.getTag().getInt(getModifierName());
		}
		return 0;
	}

	/**
	 * Returns the saved NBT value of this modifier's attribute. Used for modifiers with multiple attributes.
	 *
	 * @return 0 if this item doesnt have this modifier.
	 */
	default int getValue(ItemStack stack, Attribute a) {
		return getValue(stack);
	}

	/**
	 * Checks the Item's NBT if it has this modifier.
	 */
	default boolean itemHasModifier(ItemStack stack) {
		if (stack.hasTag()) {
			return stack.getTag().contains(getModifierName());
		}
		return false;
	}

	/**
	 * Called by Events. Listen to modifier abilities and events here.
	 */
	default void handleEventRegistry() {

	}

}
