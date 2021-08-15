package com.dmcloot.Registry;

import com.dmcloot.DMCLoot;
import com.dmcloot.Item.EssenceItem;
import com.dmcloot.Modifier.ModifierRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DMCLoot.MODID);

	public static final RegistryObject<Item> ESSENCE_COMMON = ITEMS.register("essence_" + ModifierRarity.COMMON, () -> new EssenceItem(ModifierRarity.COMMON, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static final RegistryObject<Item> ESSENCE_UNCOMMON = ITEMS.register("essence_" + ModifierRarity.UNCOMMON, () -> new EssenceItem(ModifierRarity.UNCOMMON, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static final RegistryObject<Item> ESSENCE_RARE = ITEMS.register("essence_" + ModifierRarity.RARE, () -> new EssenceItem(ModifierRarity.RARE, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static final RegistryObject<Item> ESSENCE_EPIC = ITEMS.register("essence_" + ModifierRarity.EPIC, () -> new EssenceItem(ModifierRarity.EPIC, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static final RegistryObject<Item> ESSENCE_LEGENDARY = ITEMS.register("essence_" + ModifierRarity.LEGENDARY, () -> new EssenceItem(ModifierRarity.LEGENDARY, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static final RegistryObject<Item> ESSENCE_MYTHIC = ITEMS.register("essence_" + ModifierRarity.MYTHIC, () -> new EssenceItem(ModifierRarity.MYTHIC, new Item.Properties().tab(ItemGroup.TAB_MISC)));

	public static Item getEssenceFromRarity(ModifierRarity rarity) {
		switch (rarity) {
			default:
				return ESSENCE_COMMON.get();
			case UNCOMMON:
				return ESSENCE_UNCOMMON.get();
			case RARE:
				return ESSENCE_RARE.get();
			case EPIC:
				return ESSENCE_EPIC.get();
			case LEGENDARY:
				return ESSENCE_LEGENDARY.get();
			case MYTHIC:
				return ESSENCE_MYTHIC.get();
		}
	}

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
