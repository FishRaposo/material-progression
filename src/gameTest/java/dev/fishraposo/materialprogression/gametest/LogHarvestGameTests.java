package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class LogHarvestGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(1, 1, 1);

    private LogHarvestGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Logs drop nothing when broken without an axe")
    static void emptyHandsCannotHarvestLogs(ExtendedGameTestHelper helper) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        breakBlock(helper, Blocks.OAK_LOG, ItemStack.EMPTY);
        helper.assertItemEntityNotPresent(Items.OAK_LOG, BLOCK_POS, 2.0);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The flint hatchet preserves ordinary log loot")
    static void flintHatchetHarvestsLogs(ExtendedGameTestHelper helper) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        breakBlock(
                helper,
                Blocks.OAK_LOG,
                ModItems.FLINT_HATCHET.get().getDefaultInstance()
        );
        helper.assertItemEntityCountIsAtLeast(
                Items.OAK_LOG,
                BLOCK_POS,
                2.0,
                1
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The server config restores vanilla tree punching")
    static void configOptOutRestoresLogDrops(ExtendedGameTestHelper helper) {
        ConfigFixture.setRequireAxeForLogs(helper, false);
        breakBlock(helper, Blocks.OAK_LOG, ItemStack.EMPTY);
        helper.assertItemEntityCountIsAtLeast(
                Items.OAK_LOG,
                BLOCK_POS,
                2.0,
                1
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The log rule does not affect wooden planks")
    static void woodenPlanksRemainHandHarvestable(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        breakBlock(helper, Blocks.OAK_PLANKS, ItemStack.EMPTY);
        helper.assertItemEntityCountIsAtLeast(
                Items.OAK_PLANKS,
                BLOCK_POS,
                2.0,
                1
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Nether stems follow the vanilla log tag")
    static void crimsonStemsRequireAxes(ExtendedGameTestHelper helper) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        breakBlock(helper, Blocks.CRIMSON_STEM, ItemStack.EMPTY);
        helper.assertItemEntityNotPresent(
                Items.CRIMSON_STEM,
                BLOCK_POS,
                2.0
        );
        helper.succeed();
    }

    private static void breakBlock(
            ExtendedGameTestHelper helper,
            Block block,
            ItemStack tool
    ) {
        helper.setBlock(BLOCK_POS, block);
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.gameMode.destroyBlock(helper.absolutePos(BLOCK_POS));
    }
}
