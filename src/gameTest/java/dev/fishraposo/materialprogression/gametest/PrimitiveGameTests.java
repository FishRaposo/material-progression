package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class PrimitiveGameTests {
    private static final BlockPos SUPPORT_POS = new BlockPos(1, 1, 1);
    private static final BlockPos RESOURCE_POS = SUPPORT_POS.above();

    private PrimitiveGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Ground resources are easy hand-breakable drops")
    static void groundResourceDrops(ExtendedGameTestHelper helper) {
        assertGroundResourceDrop(
                helper,
                ModBlocks.LOOSE_ROCKS.get(),
                ModItems.ROCK.get()
        );
        helper.killAllEntities();
        assertGroundResourceDrop(
                helper,
                ModBlocks.GROUND_STICK.get(),
                Items.STICK
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Ground resources cannot float without support")
    static void groundResourcesRequireSupport(ExtendedGameTestHelper helper) {
        helper.setBlock(SUPPORT_POS, Blocks.STONE);
        helper.setBlock(RESOURCE_POS, ModBlocks.LOOSE_ROCKS.get());
        helper.setBlock(SUPPORT_POS, Blocks.AIR);
        helper.assertBlockPresent(Blocks.AIR, RESOURCE_POS);
        helper.succeed();
    }

    private static void assertGroundResourceDrop(
            ExtendedGameTestHelper helper,
            Block resource,
            net.minecraft.world.item.Item expectedDrop
    ) {
        helper.setBlock(SUPPORT_POS, Blocks.STONE);
        helper.setBlock(RESOURCE_POS, resource);
        helper.breakBlock(RESOURCE_POS, ItemStack.EMPTY, helper.makeMockPlayer());
        helper.assertItemEntityCountIsAtLeast(
                expectedDrop,
                RESOURCE_POS,
                2.0,
                1
        );
    }
}
