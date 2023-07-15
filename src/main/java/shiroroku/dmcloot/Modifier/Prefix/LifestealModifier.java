package shiroroku.dmcloot.Modifier.Prefix;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LifestealModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.lifesteal";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

    public LifestealModifier() {
        super(modifierName, Affix.Prefix, new Color(236, 66, 255));
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
        MinecraftForge.EVENT_BUS.addListener(LifestealModifier::onDamageLiving);
    }

    private static void onDamageLiving(LivingDamageEvent e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.LIFESTEAL.get();
        if (e.getSource().getEntity() == null) {
            return;
        }
        if (e.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (modifier.itemHasModifier(weapon)) {
                player.heal((modifier.getValue(weapon) / 100f) * e.getAmount());
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 0.5f, 1f);
            }
        }
    }
}
