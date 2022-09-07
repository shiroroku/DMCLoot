package com.dmcloot.Item;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AffixedMetalItem extends Item {

	private final ModifierBase modifier;

	public AffixedMetalItem(ModifierBase modifierIn) {
		super(new Item.Properties().tab(DMCLoot.creativeTab).fireResistant());
		this.modifier = modifierIn;
	}

	public ModifierBase getModifier() {
		return modifier;
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
		text.add(new TranslationTextComponent("item.dmcloot.affixed_metal.desc", getModifier().getModifierAffix().name(), new TranslationTextComponent("modifier.name." + getModifier().getModifierName()).setStyle(Style.EMPTY.withColor(Color.fromRgb(getModifier().getColor().getRGB())))).setStyle(Style.EMPTY.withColor(TextFormatting.GRAY)));
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return new TranslationTextComponent("item.dmcloot.affixed_metal");
	}
}
