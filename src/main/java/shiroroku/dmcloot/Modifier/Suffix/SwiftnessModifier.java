package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.ModifierBase;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SwiftnessModifier extends ModifierBase {

    public SwiftnessModifier() {
        super("dmcloot.swiftness", Affix.Suffix, new Color(66, 227, 255));
    }

    @Override
    public List<Attribute> getAttribute() {
        return Collections.singletonList(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public List<Class<? extends Item>> getValidItemClasses() {
        return Arrays.asList(TieredItem.class, ArmorItem.class);
    }

    @Override
    public EquipmentSlot[] getValidSlotTypes(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem) {
            return new EquipmentSlot[]{((ArmorItem) itemStack.getItem()).getSlot()};
        } else {
            return new EquipmentSlot[]{EquipmentSlot.MAINHAND};
        }
    }

    @Override
    public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
        UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
        e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
