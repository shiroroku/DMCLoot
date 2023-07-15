package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LearningModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.learning";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

    public LearningModifier() {
        super(modifierName, Affix.Suffix, new Color(170, 255, 66));
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
    public void handleEventRegistry() {
        MinecraftForge.EVENT_BUS.addListener(LearningModifier::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.LEARNING.get();
        if (e.getSource().getEntity() == null) {
            return;
        }
        if (e.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (modifier.itemHasModifier(weapon)) {
                //TODO TEST THIS
                int xp = e.getEntity().getExperienceReward();
                e.getEntity().level.addFreshEntity(new ExperienceOrb(e.getEntity().level, e.getEntity().getZ(), e.getEntity().getY(), e.getEntity().getZ(), (int) (xp * modifier.getValue(weapon) / 100f)));
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.1f, (float) (6f - player.level.random.nextDouble() * 2f));
            }
        }
    }
}
