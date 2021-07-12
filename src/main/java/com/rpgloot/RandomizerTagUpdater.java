package com.rpgloot;

import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RandomizerTagUpdater {

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onItemCrafted);
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onItemPickup);
	}

	private static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
		//Works but actual item values are shown only after the item has been moved/dropped
		handleRandomizeTag(e.getCrafting());
	}

	private static void onItemPickup(PlayerEvent.ItemPickupEvent e) {
		//Does not work
		e.getOriginalEntity().setItem(handleRandomizeTag(e.getStack()));
	}

	public static ItemStack handleRandomizeTag(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT itemtag = stack.getTag();
			if (itemtag.getBoolean("rpgloot.randomize") || Configuration.MODIFY_ALL.get()) {
				IModifier.ModifierRarity rarity;
				if (itemtag.contains("rpgloot.rarity")) {
					switch (itemtag.getString("rpgloot.rarity")) {
						case "common":
							rarity = IModifier.ModifierRarity.Common;
							break;
						case "uncommon":
							rarity = IModifier.ModifierRarity.Uncommon;
							break;
						case "rare":
							rarity = IModifier.ModifierRarity.Rare;
							break;
						case "epic":
							rarity = IModifier.ModifierRarity.Epic;
							break;
						case "ledgendary":
							rarity = IModifier.ModifierRarity.Ledgendary;
							break;
						case "mythic":
							rarity = IModifier.ModifierRarity.Mythic;
							break;
						default:
							rarity = IModifier.ModifierRarity.Common;
							RPGLoot.LOGGER.error("(rpgloot.randomize): (rpgloot.rarity): Rarity specified in itemstack NBT does not exist!");
							break;
					}
					itemtag.remove("rpgloot.rarity");
				} else {
					rarity = ModifierRegistry.getRandomRarity(ModifierRegistry.randomInstance);
				}
				ModifierRegistry.applyRandomModifiersTo(ModifierRegistry.randomInstance, stack, rarity);

				itemtag.putBoolean("rpgloot.randomize", false);

				stack.setTag(itemtag);
			}
		}
		return stack;
	}
}
