package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class LogHarvestRule implements HarvestRule {
    @Override
    public void evaluate(HarvestContext context) {
        if (!canHarvest(
                context.canHarvest(),
                MaterialProgressionConfig.requireAxeForLogs(),
                context.state(),
                context.tool()
        )) {
            context.denyHarvest();
        }
    }

    public static boolean canHarvest(
            boolean vanillaCanHarvest,
            boolean requireAxeForLogs,
            BlockState state,
            ItemStack heldItem
    ) {
        if (!requireAxeForLogs || !state.is(BlockTags.LOGS)) {
            return vanillaCanHarvest;
        }
        if (heldItem.is(ItemTags.AXES)) {
            return vanillaCanHarvest;
        }
        return false;
    }
}
