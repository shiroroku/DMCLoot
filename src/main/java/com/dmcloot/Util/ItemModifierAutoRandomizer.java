package com.dmcloot.Util;

import com.dmcloot.Configuration.CommonConfiguration;
import com.dmcloot.Compat.CuriosCompat;
import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierRarity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ItemModifierAutoRandomizer {

	/**
	 * NBT TAGS:
	 * "rpgloot.randomize:(true/false)"				- Set to true to have the mod randomize the item's modifier and rarity.
	 * "rpgloot.rarity:(common/uncommon/epic...)	- Specifies the rariy the item should be. (optional)(requires randomize:true)
	 * "rpgloot.rarity_weights:(12,8,6,3,2,1)		- Specifies the chances of each rarity being applied to this item from common to mythic. (optional)(requires randomize:true)
	 */

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(ItemModifierAutoRandomizer::onItemCrafted);
		MinecraftForge.EVENT_BUS.addListener(ItemModifierAutoRandomizer::onItemToss);
		MinecraftForge.EVENT_BUS.addListener(ItemModifierAutoRandomizer::onLivingDrops);
		MinecraftForge.EVENT_BUS.addListener(ItemModifierAutoRandomizer::onContainerOpen);

	}

	private static void onContainerOpen(PlayerContainerEvent.Open e) {
		if (!e.getEntity().level.isClientSide()) {
			for (ItemStack stack : e.getContainer().getItems()) {
				processRandomize(stack);
			}
		}

	}

	private static void onLivingDrops(LivingDropsEvent e) {
		if (!e.getEntity().level.isClientSide()) {
			for (ItemEntity itementity : e.getDrops()) {
				processRandomize(itementity.getItem());
			}
		}
	}

	private static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
		if (!e.getEntity().level.isClientSide()) {
			processRandomize(e.getCrafting());
		}
	}

	private static void onItemToss(ItemTossEvent e) {
		if (!e.getPlayer().level.isClientSide()) {
			processRandomize(e.getEntityItem().getItem());
		}
	}

	private static void processRandomize(ItemStack stack) {
		//This only randomizes items with tags or curios (curios WIP)
		if (stack.hasTag() || CuriosCompat.isCurio(stack)) {
			CompoundNBT itemtag = stack.getOrCreateTag();

			//If we want to modify all possible items, tick randomize to true on items that dont have this tag.
			if (CommonConfiguration.MODIFY_ALL.get()) {
				if (!itemtag.contains("rpgloot.randomize")) {
					itemtag.putBoolean("rpgloot.randomize", true);
				}
			}

			if (itemtag.getBoolean("rpgloot.randomize")) {
				ModifierRarity rarity = null;

				//Check if the item has a rarity tag, and assign the rarity from that.
				//If it doesnt, then we check if it has rarity weights tag, and assign the rarity from that.
				if (itemtag.contains("rpgloot.rarity")) {
					String tagRarity = itemtag.getString("rpgloot.rarity");
					for (ModifierRarity r : ModifierRarity.values()) {
						if (r.toString().equals(tagRarity)) {
							rarity = r;
							break;
						}
					}
					if (rarity == null) {
						rarity = ModifierRarity.COMMON;
						DMCLoot.LOGGER.error("(rpgloot.randomize): (rpgloot.rarity): Rarity specified in itemstack NBT does not exist!");
					}

					//Remove the tag since it's not needed anymore
					itemtag.remove("rpgloot.rarity");
				} else {
					if (itemtag.contains("rpgloot.rarity_weights")) {
						String[] split = itemtag.getString("rpgloot.rarity_weights").split(",");
						int[] weights = new int[split.length];
						for (int i = 0; i < split.length; i++) {
							try {
								weights[i] = Integer.parseInt(split[i]);
							} catch (NumberFormatException nfe) {
								weights[i] = 0;
							}
						}
						rarity = ModifierHelper.getRandomRarityWithWeights(DMCLoot.randomInstance, weights);

						//Remove the tag since it's not needed anymore
						itemtag.remove("rpgloot.rarity_weights");
					}
				}

				//If we dont have the rarity or the weights tag, then just get a random rarity.
				if (rarity == null) {
					rarity = ModifierHelper.getRandomWeightedRarity(DMCLoot.randomInstance);
				}

				ModifierHelper.applyRandomModifiersTo(stack, rarity);

				//Is set to false to make sure we dont randomize items multiple times
				itemtag.putBoolean("rpgloot.randomize", false);
				stack.setTag(itemtag);
			}
		}
	}
}
