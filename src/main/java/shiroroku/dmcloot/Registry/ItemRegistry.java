package shiroroku.dmcloot.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Item.AffixedMetalItem;
import shiroroku.dmcloot.Item.EssenceItem;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Modifier.ModifierRarity;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DMCLoot.MODID);

    public static final RegistryObject<Item> ESSENCE_COMMON = ITEMS.register("essence_" + ModifierRarity.COMMON, () -> new EssenceItem(ModifierRarity.COMMON));
    public static final RegistryObject<Item> ESSENCE_UNCOMMON = ITEMS.register("essence_" + ModifierRarity.UNCOMMON, () -> new EssenceItem(ModifierRarity.UNCOMMON));
    public static final RegistryObject<Item> ESSENCE_RARE = ITEMS.register("essence_" + ModifierRarity.RARE, () -> new EssenceItem(ModifierRarity.RARE));
    public static final RegistryObject<Item> ESSENCE_EPIC = ITEMS.register("essence_" + ModifierRarity.EPIC, () -> new EssenceItem(ModifierRarity.EPIC));
    public static final RegistryObject<Item> ESSENCE_LEGENDARY = ITEMS.register("essence_" + ModifierRarity.LEGENDARY, () -> new EssenceItem(ModifierRarity.LEGENDARY));
    public static final RegistryObject<Item> ESSENCE_MYTHIC = ITEMS.register("essence_" + ModifierRarity.MYTHIC, () -> new EssenceItem(ModifierRarity.MYTHIC));

    public static Item getEssenceFromRarity(ModifierRarity rarity) {
        return switch (rarity) {
            default -> ESSENCE_COMMON.get();
            case UNCOMMON -> ESSENCE_UNCOMMON.get();
            case RARE -> ESSENCE_RARE.get();
            case EPIC -> ESSENCE_EPIC.get();
            case LEGENDARY -> ESSENCE_LEGENDARY.get();
            case MYTHIC -> ESSENCE_MYTHIC.get();
        };
    }

    public static ModifierBase getModifierFromAffixedMetal(AffixedMetalItem metal) {
        for (ModifierRegistry.MODIFIERS modifier : ModifierRegistry.MODIFIERS.values()) {
            ResourceLocation metalresource1 = ForgeRegistries.ITEMS.getKey(modifier.get().getAffixedMetal());
            ResourceLocation metalresource2 = ForgeRegistries.ITEMS.getKey(metal);
            //TODO TEST THIS
            if (metalresource1.equals(metalresource2)) {
                return modifier.get();
            }
        }
        return null;
    }

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
