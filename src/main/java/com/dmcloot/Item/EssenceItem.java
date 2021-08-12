package com.dmcloot.Item;

import com.dmcloot.Modifier.IModifier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceItem extends Item {

	private final IModifier.Rarity rarity;

	public EssenceItem(IModifier.Rarity rarityIn, Properties prop) {
		super(prop.fireResistant());
		this.rarity = rarityIn;
	}

	public IModifier.Rarity getRarity() {
		return rarity;
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
		text.add(new TranslationTextComponent("item.dmcloot.essence.desc", new TranslationTextComponent("rarity.dmcloot." + getRarity().toString())).setStyle(Style.EMPTY.withColor(TextFormatting.GRAY)));
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return new TranslationTextComponent("rarity.dmcloot." + getRarity().toString()).append(" ").append(new TranslationTextComponent("item.dmcloot.essence")).setStyle(Style.EMPTY.withColor(getRarity().getColor()));
	}
}
