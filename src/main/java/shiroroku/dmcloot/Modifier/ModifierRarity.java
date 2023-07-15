package shiroroku.dmcloot.Modifier;

import net.minecraft.ChatFormatting;
import shiroroku.dmcloot.Configuration.CommonConfiguration;

public enum ModifierRarity {
    COMMON(ChatFormatting.WHITE, "common"),
    UNCOMMON(ChatFormatting.GREEN, "uncommon"),
    RARE(ChatFormatting.BLUE, "rare"),
    EPIC(ChatFormatting.DARK_PURPLE, "epic"),
    LEGENDARY(ChatFormatting.GOLD, "legendary"),
    MYTHIC(ChatFormatting.RED, "mythic");

    private final ChatFormatting color;
    private final String name;

    ModifierRarity(ChatFormatting color, String name) {
        this.color = color;
        this.name = name;
    }

    public static int getWeight(ModifierRarity rarity) {
        return switch (rarity) {
            case COMMON -> CommonConfiguration.COMMON_WEIGHT.get();
            case UNCOMMON -> CommonConfiguration.UNCOMMON_WEIGHT.get();
            case RARE -> CommonConfiguration.RARE_WEIGHT.get();
            case EPIC -> CommonConfiguration.EPIC_WEIGHT.get();
            case LEGENDARY -> CommonConfiguration.LEGENDARY_WEIGHT.get();
            case MYTHIC -> CommonConfiguration.MYTHIC_WEIGHT.get();
        };
    }

    public ChatFormatting getColor() {
        return this.color;
    }

    public String toString() {
        return this.name;
    }
}
