package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegenerationModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.regeneration";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

    public RegenerationModifier() {
        super(modifierName, Affix.Suffix, new Color(255, 66, 145));
    }

    @Override
    public java.util.List<Attribute> getAttribute() {
        return Collections.singletonList(ATTRIBUTE.get());
    }

    @Override
    public List<Class<? extends Item>> getValidItemClasses() {
        return Arrays.asList(ArmorItem.class, ShieldItem.class);
    }

    @Override
    public EquipmentSlot[] getValidSlotTypes(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem) {
            return new EquipmentSlot[]{((ArmorItem) itemStack.getItem()).getSlot()};
        }
        return new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    }

    @Override
    public void handleEventRegistry() {
        MinecraftForge.EVENT_BUS.addListener(RegenerationModifier::onPlayerTick);
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.player.getHealth() >= e.player.getMaxHealth()) {
            return;
        }
        IModifier modifier = ModifierRegistry.MODIFIERS.REGENERATION.get();
        float baseHealing = 0.005f;

        ItemStack mainHand = e.player.getMainHandItem();
        if (modifier.itemHasModifier(mainHand)) {
            e.player.heal((modifier.getValue(mainHand) / 100f) * e.player.getMaxHealth() * baseHealing);
        }

        ItemStack offHand = e.player.getOffhandItem();
        if (modifier.itemHasModifier(offHand)) {
            e.player.heal((modifier.getValue(offHand) / 100f) * e.player.getMaxHealth() * baseHealing);
        }

        for (ItemStack armor : e.player.getArmorSlots()) {
            if (modifier.itemHasModifier(armor)) {
                e.player.heal((modifier.getValue(armor) / 100f) * e.player.getMaxHealth() * baseHealing);
            }
        }
    }
}
