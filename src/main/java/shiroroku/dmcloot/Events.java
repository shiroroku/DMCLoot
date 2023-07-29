package shiroroku.dmcloot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.dmcloot.Command.RarityDistributionsCommand;
import shiroroku.dmcloot.Item.AffixedMetalItem;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Modifier.ModifierRarity;
import shiroroku.dmcloot.Registry.ModifierRegistry;
import shiroroku.dmcloot.Util.ModifierHelper;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(modid = DMCLoot.MODID)
public class Events {

    public static void init() {
        for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
            modifier.get().handleEventRegistry();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        RarityDistributionsCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();

        if (!leftItem.isEmpty() && !rightItem.isEmpty()) {
            if (rightItem.getItem() instanceof AffixedMetalItem affixedMetal) {
				if (affixedMetal.getModifier().canApply(leftItem)) {

                    List<ModifierBase> previousModifiers = new ArrayList<>();

                    if (affixedMetal.getModifier().getModifierAffix() == IModifier.Affix.Prefix) {
                        for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(leftItem)) {
                            if (modifier.get().getModifierAffix() == IModifier.Affix.Suffix) {
                                previousModifiers.add(modifier.get());
                            }
                        }
                    } else {
                        for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(leftItem)) {
                            if (modifier.get().getModifierAffix() == IModifier.Affix.Prefix) {
                                previousModifiers.add(modifier.get());
                            }
                        }
                    }

                    ItemStack outputItem = new ItemStack(leftItem.getItem());
                    ModifierRarity itemRarity = ModifierHelper.getItemRarity(leftItem);

                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(leftItem), outputItem);
                    if (outputItem.isDamageableItem()) {
                        outputItem.setDamageValue(leftItem.getDamageValue());
                    }

                    outputItem.getTag().putBoolean("rpgloot.randomize", false);
                    affixedMetal.getModifier().applyWithRarity(outputItem, itemRarity);

                    for (ModifierBase modifier : previousModifiers) {
                        modifier.applyWithRarity(outputItem, itemRarity);
                    }

                    ModifierHelper.renameItemFromModifiers(outputItem, itemRarity);

                    event.setOutput(outputItem);
                    event.setCost(1);
                    event.setMaterialCost(1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void sortItemAttributes(ItemAttributeModifierEvent e) {
		//TODO
		/*
        if (e.getModifiers().isEmpty()) {
            return;
        }

        if (ModifierHelper.hasAnyModifier(e.getItemStack())) {
            Multimap<Attribute, AttributeModifier> unmodifiableModifiers = TreeMultimap.create(Comparator.comparing(ForgeRegistryEntry::getRegistryName), (value1, value2) -> {
                int nameCompare = Integer.compare(value1.getOperation().ordinal(), value2.getOperation().ordinal());
                int valueCompare = Double.compare(value2.getAmount(), value1.getAmount());
                return nameCompare == 0 ? valueCompare : nameCompare;
            });
            unmodifiableModifiers.putAll(e.getModifiers());
            ObfuscationReflectionHelper.setPrivateValue(ItemAttributeModifierEvent.class, e, unmodifiableModifiers, "unmodifiableModifiers");
        }*/
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent e) {
        for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
            if (modifier.get().itemHasModifier(e.getItemStack())) {
                modifier.get().handleItemAttribute(e);
            }
        }
    }

}
