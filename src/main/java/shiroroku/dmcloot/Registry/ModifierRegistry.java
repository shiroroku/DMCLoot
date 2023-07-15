package shiroroku.dmcloot.Registry;

import shiroroku.dmcloot.Modifier.ModifierBase;
import shiroroku.dmcloot.Modifier.Prefix.*;
import shiroroku.dmcloot.Modifier.Suffix.*;

public class ModifierRegistry {

    public enum MODIFIERS {
        ARCANESHIELDING(new ArcaneShieldingModifier()),
        FROST(new FrostModifier()),
        FIRE(new FireModifier()),
        LEARNING(new LearningModifier()),
        SPEED(new SpeedModifier()),
        LIFESTEAL(new LifestealModifier()),
        MENDING(new MendingModifier()),
        SWIFTNESS(new SwiftnessModifier()),
        GUARDING(new GuardingModifier()),
        REACHING(new ReachingModifier()),
        REGENERATION(new RegenerationModifier()),
        CRITICAL(new CriticalModifier()),
        LIGHTNING(new LightningModifier()),
        AQUATIC(new AquaticModifier());

        private final ModifierBase modifier;

        MODIFIERS(ModifierBase m) {
            this.modifier = m;
        }

        public ModifierBase get() {
            return this.modifier;
        }
    }
}
