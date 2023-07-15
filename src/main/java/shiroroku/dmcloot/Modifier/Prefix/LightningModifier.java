package shiroroku.dmcloot.Modifier.Prefix;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Modifier.ModifierRarity;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LightningModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.lightning";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName + ".damage", () -> new RangedAttribute("attribute.name." + modifierName + ".damage", 0.0D, 0.0D, 2048.0D));

    public LightningModifier() {
        super(modifierName, Affix.Prefix, new Color(249, 255, 66));
    }

    @Override
    public List<Attribute> getAttribute() {
        return Collections.singletonList(ATTRIBUTE.get());
    }

    @Override
    public List<Class<? extends Item>> getValidItemClasses() {
        return Arrays.asList(SwordItem.class, AxeItem.class, BowItem.class, CrossbowItem.class);
    }

    @Override
    public float getMultiplierFromRarity(ModifierRarity rarity) {
        float randomDifference = DMCLoot.randomInstance.nextInt(1) - 2;
        return switch (rarity) {
            default -> 5f;
            case COMMON -> Math.max(10f, 10f + randomDifference);
            case UNCOMMON -> 15f + randomDifference;
            case RARE -> 20f + randomDifference;
            case EPIC -> 30f + randomDifference;
            case LEGENDARY -> 40f + randomDifference;
            case MYTHIC -> Math.min(60f, 50f + randomDifference);
        };
    }

    @Override
    public void applyItemAttibute(Attribute a, ItemAttributeModifierEvent e) {
        UUID uuid = UUID.nameUUIDFromBytes((DMCLoot.MODID + "." + getModifierName() + "." + e.getSlotType().getName()).getBytes());
        e.addModifier(a, new AttributeModifier(uuid, () -> (DMCLoot.MODID + "." + getModifierName()), this.getValue(e.getItemStack(), a), AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void handleEventRegistry() {
        MinecraftForge.EVENT_BUS.addListener(LightningModifier::onDamageLiving);
    }

    @Override
    public int getValue(ItemStack item, Attribute a) {
        return getValue(item) / 2;
    }

    private static void onDamageLiving(LivingDamageEvent e) {
        IModifier mod = ModifierRegistry.MODIFIERS.LIGHTNING.get();
        if (e.getSource().getEntity() == null) {
            return;
        }
        if (e.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (mod.itemHasModifier(weapon)) {
                LivingEntity target = e.getEntity();
                if (target instanceof IronGolem || target instanceof EnderDragon || (target.isInWaterOrRain() && target.getClassification(false) != MobCategory.WATER_CREATURE) || target.getArmorCoverPercentage() > 0.0f) {
                    e.setAmount(e.getAmount() + (float) mod.getValue(weapon));
                    player.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.70f, (float) (8f - player.level.random.nextDouble() * 5f));
                }
            }
        }
    }
}
