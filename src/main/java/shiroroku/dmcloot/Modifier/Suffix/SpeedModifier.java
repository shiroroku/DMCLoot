package shiroroku.dmcloot.Modifier.Suffix;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.dmcloot.Modifier.IModifier;
import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Registry.AttributeRegistry;
import shiroroku.dmcloot.Registry.ModifierRegistry;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpeedModifier extends ModifierBase {

    private static final String modifierName = "dmcloot.speed";
    private static final RegistryObject<Attribute> ATTRIBUTE = AttributeRegistry.ATTRIBUTES.register(modifierName, () -> new RangedAttribute("attribute.name." + modifierName, 0.0D, 0.0D, 2048D));

    public SpeedModifier() {
        super(modifierName, Affix.Suffix, new Color(255, 66, 66));
    }

    @Override
    public List<Attribute> getAttribute() {
        return Collections.singletonList(ATTRIBUTE.get());
    }

    @Override
    public List<Class<? extends Item>> getValidItemClasses() {
        return Arrays.asList(PickaxeItem.class, ShovelItem.class, AxeItem.class);
    }

    @Override
    public void handleEventRegistry() {
        MinecraftForge.EVENT_BUS.addListener(SpeedModifier::onBreakSpeed);
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed e) {
        IModifier modifier = ModifierRegistry.MODIFIERS.SPEED.get();
        ItemStack item = e.getEntity().getMainHandItem();
        if (modifier.itemHasModifier(item)) {
            e.setNewSpeed(e.getOriginalSpeed() + (modifier.getValue(item) / 100f) * e.getOriginalSpeed());
        }

    }
}
