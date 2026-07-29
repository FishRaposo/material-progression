package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.world.item.BulkCraftingUpgradeItem;
import dev.fishraposo.materialprogression.world.level.block.entity.BulkCraftingTableBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
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

    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(
            description = "Bulk table plans and atomically crafts through adjacent storage"
    )
    static void recursiveCraftingUsesAdjacentInventory(
            ExtendedGameTestHelper helper
    ) {
        BulkCraftingTableBlockEntity table =
                GameTestSupport.placeBlockEntity(
                        helper,
                        GameTestSupport.DEFAULT_BLOCK_POS,
                        ModBlocks.BULK_CRAFTING_TABLE.get(),
                        BulkCraftingTableBlockEntity.class
                );
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        helper.setBlock(
                GameTestSupport.DEFAULT_BLOCK_POS.relative(Direction.EAST),
                Blocks.CHEST
        );
        ChestBlockEntity chest = helper.getBlockEntity(
                GameTestSupport.DEFAULT_BLOCK_POS.relative(Direction.EAST),
                ChestBlockEntity.class
        );
        chest.setItem(0, new ItemStack(Items.OAK_LOG, 1));

        var preview = table.preview(
                player.getInventory(),
                "minecraft:stick",
                8
        );
        helper.assertTrue(
                preview != null && preview.executable(),
                "Logs-to-sticks recursive plan was not executable"
        );
        helper.assertTrue(
                preview.plan().crafts("minecraft:oak_planks") == 1,
                "Plan did not include the log-to-planks intermediate"
        );
        helper.assertTrue(
                preview.plan().crafts("minecraft:stick") == 2,
                "Plan did not include two stick crafts"
        );
        helper.assertTrue(
                table.execute(
                        player.getInventory(),
                        "minecraft:stick",
                        8,
                        preview.fingerprint()
                ),
                "Valid recursive plan did not commit"
        );
        GameTestSupport.assertEmpty(
                helper,
                chest.getItem(0),
                "Adjacent chest input"
        );
        GameTestSupport.assertStack(
                helper,
                table.getItem(0),
                Items.STICK,
                8,
                "Bulk-crafted sticks"
        );

        for (int slot = 0; slot < table.activeBufferSlots(); slot++) {
            table.setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
        chest.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        var full = table.preview(
                player.getInventory(),
                "minecraft:stick",
                8
        );
        helper.assertTrue(
                full != null && !full.executable(),
                "Full output buffer did not reject the plan"
        );
        helper.assertFalse(
                table.execute(
                        player.getInventory(),
                        "minecraft:stick",
                        8,
                        full == null ? 0 : full.fingerprint()
                ),
                "Rejected plan mutated full storage"
        );
        GameTestSupport.assertStack(
                helper,
                chest.getItem(0),
                Items.OAK_LOG,
                1,
                "Rejected adjacent input"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(
            description = "Bulk table filter, priority, reservation, and memory modules affect jobs"
    )
    static void modulesAffectRealCraftingJobs(
            ExtendedGameTestHelper helper
    ) {
        BulkCraftingTableBlockEntity table =
                GameTestSupport.placeBlockEntity(
                        helper,
                        GameTestSupport.DEFAULT_BLOCK_POS,
                        ModBlocks.BULK_CRAFTING_TABLE.get(),
                        BulkCraftingTableBlockEntity.class
                );
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        for (var module : java.util.List.of(
                ModItems.FILTER_MODULE,
                ModItems.PRIORITY_MODULE,
                ModItems.RESERVATION_MODULE,
                ModItems.MEMORY_MODULE
        )) {
            helper.assertTrue(
                    table.installUpgrade(
                            module.get().getDefaultInstance()
                    ),
                    "Gameplay module was not installable"
            );
        }
        helper.assertTrue(
                table.addFilter("minecraft:stick"),
                "Filter target could not be configured"
        );
        helper.assertTrue(
                table.searchableTargets("stick")
                        .contains("minecraft:stick"),
                "Filter hid its whitelisted target"
        );
        helper.assertFalse(
                table.searchableTargets("crafting_table")
                        .contains("minecraft:crafting_table"),
                "Filter allowed a non-whitelisted target"
        );
        helper.assertTrue(
                table.reserve("minecraft:oak_log", 1),
                "Reservation could not be configured"
        );

        helper.setBlock(
                GameTestSupport.DEFAULT_BLOCK_POS.relative(Direction.EAST),
                Blocks.CHEST
        );
        ChestBlockEntity chest = helper.getBlockEntity(
                GameTestSupport.DEFAULT_BLOCK_POS.relative(Direction.EAST),
                ChestBlockEntity.class
        );
        chest.setItem(0, new ItemStack(Items.OAK_LOG, 2));
        table.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        var preview = table.preview(
                player.getInventory(),
                "minecraft:stick",
                8
        );
        helper.assertTrue(
                preview != null && preview.executable(),
                "Reserved inputs did not leave a craftable remainder"
        );
        helper.assertTrue(
                table.execute(
                        player.getInventory(),
                        "minecraft:stick",
                        8,
                        preview.fingerprint()
                ),
                "Module-backed job did not commit"
        );
        GameTestSupport.assertStack(
                helper,
                chest.getItem(0),
                Items.OAK_LOG,
                1,
                "Priority-selected adjacent source"
        );
        helper.assertTrue(
                table.rememberedJobs().equals(
                        java.util.List.of("minecraft:stick")
                ),
                "Memory module did not remember the completed job"
        );
        helper.succeed();
    }
}
