package com.dmcloot.Modifier;

import com.dmcloot.DMCLoot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public interface IModifier {

	enum Affix {
		Prefix,
		Suffix
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
	default float getMultiplierFromRarity(ModifierRarity rarity) {
		float randomDifference = DMCLoot.randomInstance.nextInt(6) - 5;
		switch (rarity) {
			default:
				return 1f;
			case COMMON:
				return Math.max(5f, (5f + randomDifference));
			case UNCOMMON:
				return (10f + randomDifference);
			case RARE:
				return (20f + randomDifference);
			case EPIC:
				return (40f + randomDifference);
			case LEGENDARY:
				return (70f + randomDifference);
			case MYTHIC:
				return Math.min(100f, (100f + randomDifference));
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
