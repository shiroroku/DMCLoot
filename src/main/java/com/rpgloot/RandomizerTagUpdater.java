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

	/**
	 * NBT TAGS:
	 * "rpgloot.randomize:(true/false)"				- Set to true to have the mod randomize the item's modifier and rarity.
	 * "rpgloot.rarity:(common/uncommon/epic...)	- Specifies the rariy the item should be. (optional)(requires randomize:true)
	 * "rpgloot.rarity_weights:(12,8,6,3,2,1)		- Specifies the chances of each rarity being applied to this item from common to mythic. (optional)(requires randomize:true)
	 */

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
				IModifier.Rarity rarity = null;
				if (itemtag.contains("rpgloot.rarity")) {
					String tagRarity = itemtag.getString("rpgloot.rarity");
					for (IModifier.Rarity r : IModifier.Rarity.values()) {
						if (r.toString().equals(tagRarity)) {
							rarity = r;
							break;
						}
					}
					if (rarity == null) {
						rarity = IModifier.Rarity.Common;
						RPGLoot.LOGGER.error("(rpgloot.randomize): (rpgloot.rarity): Rarity specified in itemstack NBT does not exist!");
					}
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
						rarity = ModifierRegistry.getRandomWeightedRarity(ModifierRegistry.randomInstance, weights);
						itemtag.remove("rpgloot.rarity_weights");
					}
				}
				if (rarity == null) {
					rarity = ModifierRegistry.getRandomRarity(ModifierRegistry.randomInstance);
				}
				ModifierRegistry.applyRandomModifiersTo(stack, rarity);

				itemtag.putBoolean("rpgloot.randomize", false);
				stack.setTag(itemtag);
			}
		}
	}
}
