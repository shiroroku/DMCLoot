package com.dmcloot.Mixin;

import com.dmcloot.DMCLoot;
import com.dmcloot.Item.EssenceItem;
import com.dmcloot.Registry.ItemRegistry;
import com.dmcloot.Registry.ModifierRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

		//Make sure we are in the nether, and dealing with a dmcloot item
		if (!item.level.dimensionType().piglinSafe() || !ModifierRegistry.hasAnyModifier(item.getItem())) {
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
				ItemStack essence = new ItemStack(ItemRegistry.getEssenceFromRarity(ModifierRegistry.getItemRarity(item.getItem())));
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
				DMCLoot.LOGGER.info(current.getRegistryName() + " : " + pos);
				if (current != Blocks.NETHER_BRICKS) {
					if (!pos.equals(lavaSourcePos)) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
