package shiroroku.dmcloot.Item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AffixedMetalItemColor implements ItemColor {
	@Override
	public int getColor(ItemStack stack, int index) {
		return ((AffixedMetalItem) stack.getItem()).getModifier().getColor().getRGB();
	}
}
