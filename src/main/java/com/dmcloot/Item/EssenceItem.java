package com.dmcloot.Item;

import com.dmcloot.Modifier.IModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class EssenceItem extends Item {

	private final IModifier.Rarity rarity;

	public EssenceItem(IModifier.Rarity rarityIn, Properties prop) {
		super(prop.fireResistant());
		this.rarity = rarityIn;
	}

	public IModifier.Rarity getRarity() {
		return rarity;
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return new TranslationTextComponent("rarity.dmcloot." + getRarity().toString()).append(" ").append(new TranslationTextComponent("item.dmcloot.essence")).setStyle(Style.EMPTY.withColor(getRarity().getColor()));
	}
}
