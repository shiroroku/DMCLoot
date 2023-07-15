package shiroroku.dmcloot.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiroroku.dmcloot.Configuration.CommonConfiguration;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Item.EssenceItem;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Registry.ItemRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;
import shiroroku.dmcloot.Util.ModifierHelper;

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

        handleAffixedMetalCrafting(item);
        handleEssensePressing(item);

        //Make sure we are in the nether, and dealing with a dmcloot item
        if (!item.level.dimensionType().piglinSafe() || !ModifierHelper.hasAnyModifier(item.getItem())) {
            return;
        }

        //Remove item and create essence if we are in a nether lava pool
        if (item.isInLava() && isNetherLavaPool(item.level, new BlockPos(item.getX(), item.getY(), item.getZ()))) {
            if (item.level.isClientSide) {
                item.level.playLocalSound(item.getX(), item.getY(), item.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25f, 0.30f * (item.level.random.nextFloat() * 0.5f), false);
                item.level.playLocalSound(item.getX(), item.getY(), item.getZ(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.8f, 0.02f * (item.level.random.nextFloat() * 0.5f), false);
                for (int i = 0; i < 32; ++i) {
                    item.level.addParticle(ParticleTypes.LARGE_SMOKE, item.getX() + item.level.random.nextDouble() - 0.5f, item.getY(), item.getZ() + item.level.random.nextDouble() - 0.5f, 0, 0.1D, 0);
                }
            } else {
                if (DMCLoot.randomInstance.nextDouble() <= CommonConfiguration.ESSENCE_BURNING_CHANCE.get()) {
                    ItemStack essence = new ItemStack(ItemRegistry.getEssenceFromRarity(ModifierHelper.getItemRarity(item.getItem())));
                    item.level.addFreshEntity(new ItemEntity(item.level, item.getX(), item.getY(), item.getZ(), essence));
                }
                item.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    /**
     * Checks if the given position is a lava block surrounded by eight nether bricks.
     */
    private boolean isNetherLavaPool(Level level, BlockPos lavaSourcePos) {
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

    private void handleAffixedMetalCrafting(ItemEntity item) {
        if (CommonConfiguration.ENABLE_AFFIXED_METAL_CRAFTING.get() && ModifierHelper.hasAnyModifier(item.getItem()) && item.level.getBlockState(new BlockPos(item.getX(), item.getY(), item.getZ())).getBlock() == Blocks.SOUL_FIRE) {
            double prefixChance = DMCLoot.randomInstance.nextDouble();
            double suffixChance = DMCLoot.randomInstance.nextDouble();

            boolean didAny = false;

            List<ModifierRegistry.MODIFIERS> modifiers = ModifierHelper.getAllModifiers(item.getItem());
            for (ModifierRegistry.MODIFIERS modifier : modifiers) {
                if (modifier.get().getModifierAffix() == IModifier.Affix.Prefix) {
                    if (prefixChance <= CommonConfiguration.AFFIXED_METAL_PREFIX_BURNING_CHANCE.get()) {
                        item.level.addFreshEntity(new ItemEntity(item.level, item.getX(), item.getY(), item.getZ(), new ItemStack(modifier.get().getAffixedMetal())));
                        didAny = true;
                    }
                } else {
                    if (modifier.get().getModifierAffix() == IModifier.Affix.Suffix) {
                        if (suffixChance <= CommonConfiguration.AFFIXED_METAL_SUFFIX_BURNING_CHANCE.get()) {
                            item.level.addFreshEntity(new ItemEntity(item.level, item.getX(), item.getY(), item.getZ(), new ItemStack(modifier.get().getAffixedMetal())));
                            didAny = true;
                        }
                    }
                }
            }

            if (didAny) {
                item.remove(Entity.RemovalReason.KILLED);
            }

        }
    }

    private void handleEssensePressing(ItemEntity item) {
        BlockState above = item.level.getBlockState(item.blockPosition().above());
        if ((above.getBlock() == Blocks.PISTON || above.getBlock() == Blocks.MOVING_PISTON || above.getBlock() == Blocks.PISTON_HEAD) && item.level.getBlockState(item.blockPosition().below()).getBlock() == Blocks.SMITHING_TABLE) {

            //Freeze item on smithing table.
            item.setDeltaMovement(item.getDeltaMovement().multiply(new Vec3(0, 0, 0)));
            //TODO TEST THIS
            item.setPos(item.blockPosition().getX() + 0.5f, item.blockPosition().getY(), item.blockPosition().getZ() + 0.5f);

            //When piston is extended downwards.
            if (!item.level.isClientSide && item.getItem().getItem() instanceof EssenceItem && above.getValue(PistonBaseBlock.FACING).equals(Direction.DOWN) && above.getValues().containsKey(PistonBaseBlock.EXTENDED) && above.getValue(PistonBaseBlock.EXTENDED)) {
                if (item.level.getBlockState(item.blockPosition()).getBlock() == Blocks.MOVING_PISTON) {

                    //Get all items in the blockspace above smithing table.
                    //if we have more than one type of essence then stop.
                    //find the first item that have any modifier applied to it.
                    AABB aabb = new AABB(item.blockPosition().getX(), item.blockPosition().getY(), item.blockPosition().getZ(), item.blockPosition().getX() + 1, item.blockPosition().getY() + 1, item.blockPosition().getZ() + 1);
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

							//TODO TEST THIS
                            TagKey<Item> essenceTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(DMCLoot.MODID, "essence"));
                            if (currentItem.getTags().anyMatch(t -> t.equals(essenceTag)) && e != item) {
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
