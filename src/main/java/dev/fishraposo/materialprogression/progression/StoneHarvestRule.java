package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public final class StoneHarvestRule implements HarvestRule {
    private static final int ROCKS_PER_STONE = 4;

    @Override
    public void evaluate(HarvestContext context) {
        if (!context.hasDrops()
                || !MaterialProgressionConfig.stoneRockHarvesting()
                || !context.state().is(Blocks.STONE)) {
            return;
        }
        context.replaceDrops(new ItemStack(
                ModItems.ROCK.get(),
                ROCKS_PER_STONE
        ));
    }
}
