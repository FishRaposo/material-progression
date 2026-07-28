package dev.fishraposo.materialprogression.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class GameTestSupport {
    static final BlockPos DEFAULT_BLOCK_POS = new BlockPos(1, 1, 1);

    private GameTestSupport() {
    }

    static <T extends BlockEntity> T placeBlockEntity(
            ExtendedGameTestHelper helper,
            BlockPos position,
            Block block,
            Class<T> blockEntityType
    ) {
        helper.setBlock(position, block);
        return helper.getBlockEntity(position, blockEntityType);
    }

    static void assertEmpty(
            ExtendedGameTestHelper helper,
            ItemStack stack,
            String context
    ) {
        helper.assertTrue(stack.isEmpty(), context + " was not empty");
    }

    static void assertStack(
            ExtendedGameTestHelper helper,
            ItemStack stack,
            Item expectedItem,
            int expectedCount,
            String context
    ) {
        helper.assertTrue(
                stack.is(expectedItem),
                context + " contained the wrong item: " + stack
        );
        helper.assertTrue(
                stack.getCount() == expectedCount,
                context + " contained " + stack.getCount()
                        + " items instead of " + expectedCount
        );
    }
}
