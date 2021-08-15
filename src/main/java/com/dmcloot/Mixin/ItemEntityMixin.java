package com.dmcloot.Mixin;

import com.dmcloot.DMCLoot;
import com.dmcloot.Item.EssenceItem;
import com.dmcloot.Modifier.IModifier;
import com.dmcloot.Registry.ItemRegistry;
import com.dmcloot.Util.ModifierHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@Inject(at = @At("TAIL"), method = "tick()V", cancellable = true)
	public void tick(CallbackInfo callback) {
		ItemEntity item = (ItemEntity) (Object) this;

		//Make essense float in lava
		if (item.getItem().getItem() instanceof EssenceItem && item.isInLava()) {
			item.setDeltaMovement(item.getDeltaMovement().multiply(1.0D, 1.02D, 1.0D));
			return;
		}

		handleEssenseCrafting(item);

		//Make sure we are in the nether, and dealing with a dmcloot item
		if (!item.level.dimensionType().piglinSafe() || !ModifierHelper.hasAnyModifier(item.getItem())) {
			return;
		}

		//Remove item and create essence if we are in a nether lava pool
		if (item.isInLava() && isNetherLavaPool(item.level, new BlockPos(item.getX(), item.getY(), item.getZ()))) {
			if (item.level.isClientSide) {
				item.level.playLocalSound(item.getX(), item.getY(), item.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 0.30f * (item.level.random.nextFloat() * 0.5f), false);
				item.level.playLocalSound(item.getX(), item.getY(), item.getZ(), SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.8f, 0.02f * (item.level.random.nextFloat() * 0.5f), false);
				for (int i = 0; i < 32; ++i) {
					item.level.addParticle(ParticleTypes.LARGE_SMOKE, item.getX() + item.level.random.nextDouble() - 0.5f, item.getY(), item.getZ() + item.level.random.nextDouble() - 0.5f, 0, 0.1D, 0);
				}
			} else {
				ItemStack essence = new ItemStack(ItemRegistry.getEssenceFromRarity(ModifierHelper.getItemRarity(item.getItem())));
				item.level.addFreshEntity(new ItemEntity(item.level, item.getX(), item.getY(), item.getZ(), essence));
				item.remove();
			}
		}
	}

	/**
	 * Checks if the given position is a lava block surrounded by eight nether bricks.
	 */
	private boolean isNetherLavaPool(World level, BlockPos lavaSourcePos) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos pos = new BlockPos(lavaSourcePos.getX() + x, lavaSourcePos.getY(), lavaSourcePos.getZ() + z);
				Block current = level.getBlockState(pos).getBlock();
				if (current != Blocks.NETHER_BRICKS) {
					if (!pos.equals(lavaSourcePos)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void handleEssenseCrafting(ItemEntity item) {
		BlockState above = item.level.getBlockState(item.blockPosition().above());
		if ((above.getBlock() == Blocks.PISTON || above.getBlock() == Blocks.MOVING_PISTON || above.getBlock() == Blocks.PISTON_HEAD) && item.level.getBlockState(item.blockPosition().below()).getBlock() == Blocks.SMITHING_TABLE) {

			//Freeze item on smithing table.
			item.setDeltaMovement(item.getDeltaMovement().multiply(new Vector3d(0, 0, 0)));
			item.setPosAndOldPos(item.blockPosition().getX() + 0.5f, item.blockPosition().getY(), item.blockPosition().getZ() + 0.5f);

			//When piston is extended downwards.
			if (!item.level.isClientSide && item.getItem().getItem() instanceof EssenceItem && above.getValue(PistonBlock.FACING).equals(Direction.DOWN) && above.getValue(PistonBlock.EXTENDED)) {
				if (item.level.getBlockState(item.blockPosition()).getBlock() == Blocks.MOVING_PISTON) {

					//Get all items in the blockspace above smithing table.
					//if we have more than one type of essence then stop.
					//find the first item that have any modifier applied to it.
					AxisAlignedBB aabb = new AxisAlignedBB(item.blockPosition().getX(), item.blockPosition().getY(), item.blockPosition().getZ(), item.blockPosition().getX() + 1, item.blockPosition().getY() + 1, item.blockPosition().getZ() + 1);
					List<Entity> entitiesInBlock = item.level.getEntities(null, aabb);
					ItemEntity foundItem = null;
					for (Entity e : entitiesInBlock) {
						if (e instanceof ItemEntity) {

							ItemStack currentItem = ((ItemEntity) e).getItem();
							boolean canSupportPrefix = ModifierHelper.getRandomModifierFor(IModifier.Affix.Prefix, currentItem, false) != null;
							boolean canSupportSuffix = ModifierHelper.getRandomModifierFor(IModifier.Affix.Suffix, currentItem, false) != null;

							if (canSupportPrefix || canSupportSuffix) {
								foundItem = (ItemEntity) e;
							}

							if (currentItem.getItem().getTags().contains(new ResourceLocation(DMCLoot.MODID, "essence")) && e != item) {
								return;
							}
						}
					}

					//If we have an item, apply random modifier of the essence rarity.
					//Copy over enchantments.
					//Add new item to the world and shrink the previous.
					if (foundItem != null) {
						ItemStack newItem = new ItemStack(foundItem.getItem().getItem());
						newItem.getTag().putBoolean("rpgloot.randomize", false);
						ModifierHelper.applyRandomModifiersTo(newItem, ((EssenceItem) item.getItem().getItem()).getRarity());
						EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(foundItem.getItem()), newItem);
						item.level.addFreshEntity(new ItemEntity(item.level, item.getX(), item.getY(), item.getZ(), newItem));
						foundItem.getItem().shrink(1);
						item.getItem().shrink(1);
					}
				}
			}
		}

	}
}
