package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.world.item.BulkCraftingUpgradeItem;
import dev.fishraposo.materialprogression.world.level.block.entity.BulkCraftingTableBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class BulkCraftingTableGameTests {
    private BulkCraftingTableGameTests() {
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(
            description = "Bulk table modules enforce budget, tiers, and hopper-visible storage"
    )
    static void modulesAndBufferArePlayable(
            ExtendedGameTestHelper helper
    ) {
        BulkCraftingTableBlockEntity table =
                GameTestSupport.placeBlockEntity(
                        helper,
                        GameTestSupport.DEFAULT_BLOCK_POS,
                        ModBlocks.BULK_CRAFTING_TABLE.get(),
                        BulkCraftingTableBlockEntity.class
                );
        helper.assertTrue(
                table.activeBufferSlots() == 9,
                "Base table did not expose nine buffer slots"
        );

        ItemStack basicStorage =
                ModItems.STORAGE_MODULE.get().getDefaultInstance();
        helper.assertTrue(
                table.installUpgrade(basicStorage),
                "Basic storage module was not installable"
        );
        helper.assertTrue(
                table.activeBufferSlots() == 12,
                "Basic storage module did not add three slots"
        );
        ItemStack advancedStorage =
                ModItems.ADVANCED_STORAGE_MODULE.get().getDefaultInstance();
        helper.assertTrue(
                table.installUpgrade(advancedStorage),
                "Advanced storage module did not replace its lower tier"
        );
        helper.assertTrue(
                table.activeBufferSlots() == 18,
                "Advanced storage module did not expose the full buffer"
        );

        ItemStack priority =
                ModItems.PRIORITY_MODULE.get().getDefaultInstance();
        helper.assertTrue(
                table.installUpgrade(priority),
                "Priority module was not installable"
        );
        helper.assertFalse(
                table.installUpgrade(
                        ModItems.PRIORITY_MODULE.get().getDefaultInstance()
                ),
                "Duplicate binary priority capability was installed"
        );
        helper.assertTrue(
                table.moduleCapacity(
                        BulkCraftingUpgradeItem.Family.PRIORITY
                ) == 1,
                "Priority capability did not resolve once"
        );

        table.setItem(0, new ItemStack(Items.OAK_LOG, 8));
        helper.assertTrue(
                table.getSlotsForFace(Direction.UP).length == 18,
                "Hopper-facing slot exposure ignored storage upgrades"
        );
        helper.assertTrue(
                table.canPlaceItemThroughFace(
                        1,
                        Items.OAK_LOG.getDefaultInstance(),
                        Direction.UP
                ),
                "Hopper insertion into the active buffer was rejected"
        );
        helper.assertTrue(
                table.canTakeItemThroughFace(
                        0,
                        table.getItem(0),
                        Direction.DOWN
                ),
                "Hopper extraction from the buffer was rejected"
        );
        helper.succeed();
    }
}
