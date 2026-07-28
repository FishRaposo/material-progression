package dev.fishraposo.materialprogression.progression;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class LogHarvestRule {
    private LogHarvestRule() {
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
