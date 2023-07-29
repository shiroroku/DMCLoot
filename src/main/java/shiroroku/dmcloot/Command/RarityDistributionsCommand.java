package shiroroku.dmcloot.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import shiroroku.dmcloot.DMCLoot;
import shiroroku.dmcloot.Modifier.ModifierRarity;
import shiroroku.dmcloot.Util.ItemModifierAutoRandomizer;

import java.util.HashMap;
import java.util.Map;

public class RarityDistributionsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dmclootRarityDistributions")
                .requires(cs -> cs.hasPermission(4))
                .then(Commands.argument("iterations", IntegerArgumentType.integer(1)).executes(cs -> {
                    int iterations = IntegerArgumentType.getInteger(cs, "iterations");
                    DMCLoot.LOGGER.info("Running dmclootRarityDistributions for {} iterations...", iterations);
                    HashMap<String, Integer> rarity_counts = new HashMap<>();
                    for (int i = 0; i < iterations; i++) {
                        ItemStack testitem = new ItemStack(Items.IRON_SWORD);
                        ItemModifierAutoRandomizer.processRandomize(testitem);
                        String rarity = getItemRarity(testitem);
                        rarity_counts.put(rarity, rarity_counts.getOrDefault(rarity, 0) + 1);
                    }

                    rarity_counts.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> DMCLoot.LOGGER.info("%-10s:%10d".formatted(entry.getKey(), entry.getValue())));
                    return 0;
                })));
    }

    private static String getItemRarity(ItemStack item) {
        if (item.hasTag() && item.getTag().contains("dmcloot.rarity") && item.getTag().getString("dmcloot.rarity") != null) {
            for (ModifierRarity r : ModifierRarity.values()) {
                if (r.toString().equals(item.getTag().getString("dmcloot.rarity"))) {
                    return r.name();
                }
            }
        }
        return "EMPTY";
    }
}
