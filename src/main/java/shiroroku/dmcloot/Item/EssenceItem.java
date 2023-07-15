package shiroroku.dmcloot.Item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.ModifierRarity;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceItem extends Item {

    private final ModifierRarity rarity;

    public EssenceItem(ModifierRarity rarityIn) {
        super(new Item.Properties().tab(DMCLoot.creativeTab).fireResistant());
        this.rarity = rarityIn;
    }

    public ModifierRarity getRarity() {
        return rarity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.dmcloot.essence.desc", Component.translatable("rarity.dmcloot." + getRarity().toString())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("rarity.dmcloot." + getRarity().toString()).append(" ").append(Component.translatable("item.dmcloot.essence")).setStyle(Style.EMPTY.withColor(getRarity().getColor()));
    }
}
