package com.rpgloot.Modifier;

import com.rpgloot.RPGLoot;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.UUID;

public interface IModifier {

	enum ModifierType {
		Prefix,
		Suffix
	}

	enum ModifierRarity {
		Common(TextFormatting.WHITE),
		Uncommon(TextFormatting.GREEN),
		Rare(TextFormatting.BLUE),
		Epic(TextFormatting.DARK_PURPLE),
		Ledgendary(TextFormatting.GOLD),
		Mythic(TextFormatting.RED);

		private final TextFormatting color;

		ModifierRarity(TextFormatting color) {
			this.color = color;
		}

		public TextFormatting getColor() {
			return this.color;
		}
	}

	/**
	 * Returns whether the modifier is a prefix or a suffix.
	 */
	ModifierType getModifierType();

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
		float randomDifference = ModifierRegistry.randomInstance.nextInt(6) - 5;
		switch (rarity) {
			default:
				return 1f;
			case Common:
				return Math.max(5f, 5f + randomDifference);
			case Uncommon:
				return 10f + randomDifference;
			case Rare:
				return 20f + randomDifference;
			case Epic:
				return 40f + randomDifference;
			case Ledgendary:
				return 70f + randomDifference;
			case Mythic:
				return Math.min(100f, 100f + randomDifference);
		}
	}

	/**
	 * The name of the modifier, must be NBT friendly as it's used to indicate this modifiers presence.
	 */
	String getModifierName();

	/**
	 * Points to the registered Attribute for this modifier.
	 */
	Attribute getAttribute();

	/**
	 * Slot(s) that this modifier is active in.
	 */
	default EquipmentSlotType[] getValidSlotTypes(ItemStack item) {
		return new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
	}

	/**
	 * Gets avaliable item classes that this modifier can be applied to. Return null for all items.
	 */
	default List<Class<? extends Item>> getValidItemTypes() {
		return null;
	}

	/**
	 * The default strength value of this modifier.
	 */
	default Object getDefaultValue() {
		return 1;
	}

	/**
	 * Handles what items the modifier's Attribute should be applied to.
	 */
	default void handleItemAttribute(ItemAttributeModifierEvent e) {
		for (EquipmentSlotType slot : getValidSlotTypes(e.getItemStack())) {
			if (e.getSlotType() == slot) {
				if (!e.getModifiers().containsKey(getAttribute())) {
					if (canApply(e.getItemStack())) {
						e.addModifier(getAttribute(), new AttributeModifier(UUID.nameUUIDFromBytes((RPGLoot.MODID + "." + getModifierName()).getBytes()), () -> (RPGLoot.MODID + "." + getModifierName()), e.getItemStack().getTag().getInt(getModifierName()), AttributeModifier.Operation.ADDITION));
					}
				}
			}
		}
	}

	/**
	 * Returns if the stack can support this modifier. Use getValidSlotTypes to specify classes.
	 */
	default boolean canApply(ItemStack stack) {
		List<Class<? extends Item>> itemtypes = getValidItemTypes();
		boolean add = itemtypes == null || itemtypes.size() == 0;
		for (Class<? extends Item> type : itemtypes) {
			if (type.isAssignableFrom(stack.getItem().getClass())) {
				add = true;
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
	default void applyWithRarity(ItemStack stack, ModifierRarity rarity) {
		apply(stack, (int) ((Integer) getDefaultValue() * getMultiplierFromRarity(rarity)));
	}

	/**
	 * Returns the saved NBT value of this modifier. Object is integer by default.
	 *
	 * @return Null if this item doesnt have this modifier.
	 */
	default Object getValue(ItemStack stack) {
		if (itemHasModifier(stack)) {
			return stack.getTag().getInt(getModifierName());
		}
		return null;
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
