package shiroroku.dmcloot.Modifier.Prefix;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MendingModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.mending";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

    public MendingModifier() {
        super(modifierName, Affix.Prefix, new Color(120, 255, 66));
    }

    @Override
    public List<Attribute> getAttribute() {
        return Collections.singletonList(ATTRIBUTE.get());
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
    public void handleEventRegistry() {
        MinecraftForge.EVENT_BUS.addListener(MendingModifier::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(MendingModifier::onDamageLiving);
        MinecraftForge.EVENT_BUS.addListener(MendingModifier::onLivingHurt);
        //MinecraftForge.EVENT_BUS.addListener(MendingModifier::onUseHoeEvent);

    }

    private static void processModifier(IModifier modifier, RandomSource r, ItemStack stack) {
        if (modifier.itemHasModifier(stack) && stack.isDamageableItem() && stack.getDamageValue() > 0) {
            if (r.nextDouble() < (modifier.getValue(stack) / 100f)) {
                stack.setDamageValue(stack.getDamageValue() - 2);
            }
        }
    }

    /**
     * When the player uses a hoe.
     */
    //TODO FIX THIS
//    @SuppressWarnings("deprecation")
//    private static void onUseHoeEvent(UseHoeEvent e) {
//        IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
//        processModifier(modifier, e.getPlayer().getRandom(), e.getPlayer().getMainHandItem());
//    }

    /**
     * When the player breaks a block, heal mainhand
     */
    private static void onBlockBreak(BlockEvent.BreakEvent e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
        processModifier(modifier, e.getPlayer().getRandom(), e.getPlayer().getMainHandItem());
    }

    /**
     * When the player gets hurt, heal armor
     */
    private static void onLivingHurt(LivingHurtEvent e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
        Iterable<ItemStack> items = e.getEntity().getArmorSlots();
        for (ItemStack stack : items) {
            processModifier(modifier, e.getEntity().getRandom(), stack);
        }
    }

    /**
     * When the player attacks, heal mainhand
     */
    private static void onDamageLiving(LivingDamageEvent e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.MENDING.get();
        if (e.getSource().getEntity() == null) {
            return;
        }
        if (e.getSource().getEntity() instanceof Player) {
            processModifier(modifier, e.getEntity().getRandom(), ((Player) e.getSource().getEntity()).getMainHandItem());
        }
    }
}
