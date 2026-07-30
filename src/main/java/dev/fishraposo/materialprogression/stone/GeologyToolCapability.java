package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.registry.ModTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class GeologyToolCapability {
    private GeologyToolCapability() {
    }

    public static boolean canMine(
            ItemStack tool,
            BlockState target,
            GeologyTier tier
    ) {
        if ((!tool.is(ItemTags.PICKAXES) && !tool.is(ModTags.HAMMERS))
                || !tool.isCorrectToolForDrops(target)) {
            return false;
        }

        // These vanilla blocks are capability probes for the Tool component,
        // not an item allowlist. Modded picks and tagged hammers qualify when
        // their own correct-for-drops rules meet the same vanilla tiers.
        return switch (tier) {
            case LEVEL_0 -> true;
            case LEVEL_1 ->
                    tool.isCorrectToolForDrops(Blocks.IRON_ORE.defaultBlockState());
            case LEVEL_2 ->
                    tool.isCorrectToolForDrops(Blocks.DIAMOND_ORE.defaultBlockState());
            case LEVEL_3 ->
                    tool.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState());
        };
    }
}
