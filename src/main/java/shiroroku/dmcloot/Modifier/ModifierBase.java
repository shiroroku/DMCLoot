package shiroroku.dmcloot.Modifier;


import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Configuration.CommonConfiguration;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Item.AffixedMetalItem;
import shiroroku.dmcloot.Registry.ItemRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ModifierBase implements IModifier {

    private ForgeConfigSpec.ConfigValue<List<String>> ADDITIONS = null;
    private ForgeConfigSpec.IntValue WEIGHT = null;
    private ForgeConfigSpec.DoubleValue STRENGTH = null;
    private final RegistryObject<Item> AFFIXED_METAL;
    private final Color color;
    private final String name;
    private final Affix affix;

    public ModifierBase(String name, Affix affix, Color color) {
        this.name = name;
        this.affix = affix;
        this.color = color;
        AFFIXED_METAL = ItemRegistry.ITEMS.register("affixed_metal_" + name.split("\\.")[1], () -> new AffixedMetalItem(this));
    }

    public Color getColor() {
        return color;
    }

    public Item getAffixedMetal() {
        return AFFIXED_METAL.get();
    }

    @Override
    public String getModifierName() {
        return name;
    }

    @Override
    public Affix getModifierAffix() {
        return affix;
    }

    public void buildAdditionsConfig(ForgeConfigSpec.Builder builder) {
        ADDITIONS = builder.define(this.getModifierName().split("\\.")[1], new ArrayList<>());
    }

    public void buildStrengthConfig(ForgeConfigSpec.Builder builder) {
        STRENGTH = builder.defineInRange(this.getModifierName().split("\\.")[1], 1, 0, Double.MAX_VALUE);
    }

    public void buildWeightConfig(ForgeConfigSpec.Builder builder) {
        WEIGHT = builder.defineInRange(this.getModifierName().split("\\.")[1], 5, 0, Integer.MAX_VALUE);
    }

    public int getWeight() {
        return WEIGHT.get();
    }

    public List<String> getAdditions() {
        return ADDITIONS.get();
    }

    public double getStrengthMultiplier() {
        return STRENGTH.get();
    }

    /**
     * Applies this modifier and changes value depending on rarity.
     */
    public void applyWithRarity(ItemStack stack, ModifierRarity rarity) {
        float globalStrength = CommonConfiguration.GLOBAL_STRENGTH_MODIFIER.get().floatValue();
        apply(stack, (int) (getMultiplierFromRarity(rarity) * globalStrength * getStrengthMultiplier()));
    }

    /**
     * Applies this modifier and NBT value to the given ItemStack.
     */
    public void apply(ItemStack stack, int value) {
        if (canApply(stack)) {
            stack.getOrCreateTag().putInt(getModifierName(), value);
        }
    }

    /**
     * Returns if the stack can support this modifier. Use getValidItemClasses to specify classes.
     */
    public boolean canApply(ItemStack stack) {
        List<String> additions = getAdditions();
        List<Class<? extends Item>> itemtypes = getValidItemClasses();
        boolean add = itemtypes == null || itemtypes.size() == 0;
        if (!add) {
            for (Class<? extends Item> type : itemtypes) {
                if (type.isAssignableFrom(stack.getItem().getClass())) {
                    add = true;
                }
            }
        }
        for (String id : additions) {
            if (!id.isEmpty()) {
                //TODO TEST THIS
                if (id.startsWith("#")) {
                    if (ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation.tryParse(id.replace("#", "")))).contains(stack.getItem())) {
                        add = true;
                    }
                } else {
                    Item fromreg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
                    if (fromreg != null && fromreg == stack.getItem()) {
                        add = true;
                    }
                }
            }
        }
        return add;
    }

    /**
     * Handles what items the modifier's Attribute should be applied to.
     */
    public void handleItemAttribute(ItemAttributeModifierEvent e) {
        for (EquipmentSlot slot : getValidSlotTypes(e.getItemStack())) {
            if (e.getSlotType() == slot) {
                for (Attribute a : getAttribute()) {
                    if (!e.getModifiers().containsKey(a)) {
                        if (canApply(e.getItemStack())) {
                            applyItemAttibute(a, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Applies Attribute to items that have this modifier.
     */
    public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
        UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
        e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a) / 100f, AttributeModifier.Operation.MULTIPLY_BASE));
    }

}
