package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.ModifierBase;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuardingModifier extends ModifierBase {

    public GuardingModifier() {
        super("dmcloot.guarding", Affix.Suffix, new Color(66, 255, 75));
    }

    @Override
    public List<Attribute> getAttribute() {
        return Collections.singletonList(Attributes.ARMOR);
    }

    @Override
    public List<Class<? extends Item>> getValidItemClasses() {
        return Arrays.asList(TieredItem.class, ShieldItem.class);
    }

    @Override
    public EquipmentSlot[] getValidSlotTypes(ItemStack itemStack) {
        return new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    }

    @Override
    public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
        UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
        e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
