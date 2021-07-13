package com.rpgloot;

import com.rpgloot.Modifier.IModifier;
import com.rpgloot.Registry.ModifierRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RandomizerTagUpdater {

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onItemCrafted);
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onItemToss);
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onLivingDrops);
		MinecraftForge.EVENT_BUS.addListener(RandomizerTagUpdater::onContainerOpen);

	}

	private static void onContainerOpen(PlayerContainerEvent.Open e) {
		if (!e.getEntity().level.isClientSide()) {
			for (ItemStack stack : e.getContainer().getItems()) {
				handleRandomizeTag(stack);
			}
		}

	}

	private static void onLivingDrops(LivingDropsEvent e) {
		if (!e.getEntity().level.isClientSide()) {
			for (ItemEntity itementity : e.getDrops()) {
				handleRandomizeTag(itementity.getItem());
			}
		}
	}

	private static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
		if (!e.getEntity().level.isClientSide()) {
			handleRandomizeTag(e.getCrafting());
		}
	}

	private static void onItemToss(ItemTossEvent e) {
		if (!e.getPlayer().level.isClientSide()) {
			handleRandomizeTag(e.getEntityItem().getItem());
		}
	}

	public static void handleRandomizeTag(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT itemtag = stack.getTag();
			if (Configuration.MODIFY_ALL.get()) {
				if (!itemtag.contains("rpgloot.randomize")) {
					itemtag.putBoolean("rpgloot.randomize", true);
				}
			}

			if (itemtag.getBoolean("rpgloot.randomize")) {
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
	}
}
