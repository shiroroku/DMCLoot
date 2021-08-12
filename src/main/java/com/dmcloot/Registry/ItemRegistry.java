package com.dmcloot.Registry;

import com.dmcloot.DMCLoot;
import com.dmcloot.Item.EssenceItem;
import com.dmcloot.Modifier.IModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DMCLoot.MODID);

	public static RegistryObject<Item> ESSENCE_COMMON = ITEMS.register("essence_" + IModifier.Rarity.Common, () -> new EssenceItem(IModifier.Rarity.Common, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static RegistryObject<Item> ESSENCE_UNCOMMON = ITEMS.register("essence_" + IModifier.Rarity.Uncommon, () -> new EssenceItem(IModifier.Rarity.Uncommon, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static RegistryObject<Item> ESSENCE_RARE = ITEMS.register("essence_" + IModifier.Rarity.Rare, () -> new EssenceItem(IModifier.Rarity.Rare, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static RegistryObject<Item> ESSENCE_EPIC = ITEMS.register("essence_" + IModifier.Rarity.Epic, () -> new EssenceItem(IModifier.Rarity.Epic, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static RegistryObject<Item> ESSENCE_LEGENDARY = ITEMS.register("essence_" + IModifier.Rarity.Legendary, () -> new EssenceItem(IModifier.Rarity.Legendary, new Item.Properties().tab(ItemGroup.TAB_MISC)));
	public static RegistryObject<Item> ESSENCE_MYTHIC = ITEMS.register("essence_" + IModifier.Rarity.Mythic, () -> new EssenceItem(IModifier.Rarity.Mythic, new Item.Properties().tab(ItemGroup.TAB_MISC)));

	public static Item getEssenceFromRarity(IModifier.Rarity rarity) {
		switch (rarity) {
			default:
				return ESSENCE_COMMON.get();
			case Uncommon:
				return ESSENCE_UNCOMMON.get();
			case Rare:
				return ESSENCE_RARE.get();
			case Epic:
				return ESSENCE_EPIC.get();
			case Legendary:
				return ESSENCE_LEGENDARY.get();
			case Mythic:
				return ESSENCE_MYTHIC.get();
		}
	}

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
