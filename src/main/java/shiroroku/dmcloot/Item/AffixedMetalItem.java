package shiroroku.dmcloot.Item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.ModifierBase;

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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.dmcloot.affixed_metal.desc", getModifier().getModifierAffix().name(), Component.translatable("modifier.name." + getModifier().getModifierName()).setStyle(Style.EMPTY.withColor(getModifier().getColor().getRGB()))).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.dmcloot.affixed_metal");
    }

}
