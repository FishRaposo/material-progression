package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.progression.HarvestContext;
import dev.fishraposo.materialprogression.progression.HarvestRuleRegistry;
import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class PrimitiveProgressionGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(1, 1, 1);

    private PrimitiveProgressionGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A tagged knife turns suitable plants into fiber")
    static void knifeHarvestsPlantFiber(ExtendedGameTestHelper helper) {
        ConfigFixture.setKnifePlantHarvesting(helper, true);
        breakBlock(
                helper,
                Blocks.FERN,
                ModItems.FLINT_KNIFE.get().getDefaultInstance()
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.PLANT_FIBER.get(),
                BLOCK_POS,
                2.0,
                1
        );
        helper.assertItemEntityNotPresent(Items.FERN, BLOCK_POS, 2.0);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Ordinary tools preserve vanilla plant drops")
    static void ordinaryPlantHarvestRemainsVanilla(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setKnifePlantHarvesting(helper, true);
        breakBlock(helper, Blocks.VINE, new ItemStack(Items.SHEARS));
        helper.assertItemEntityCountIsAtLeast(
                Items.VINE,
                BLOCK_POS,
                2.0,
                1
        );
        helper.assertItemEntityNotPresent(
                ModItems.PLANT_FIBER.get(),
                BLOCK_POS,
                2.0
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Natural stone drops four Rocks")
    static void naturalStoneDropsRocks(ExtendedGameTestHelper helper) {
        ConfigFixture.setStoneRockHarvesting(helper, true);
        breakBlock(helper, Blocks.STONE, new ItemStack(Items.WOODEN_PICKAXE));
        helper.assertItemEntityCountIsAtLeast(
                ModItems.ROCK.get(),
                BLOCK_POS,
                2.0,
                4
        );
        helper.assertItemEntityNotPresent(
                Items.COBBLESTONE,
                BLOCK_POS,
                2.0
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Non-natural stone variants keep vanilla drops")
    static void nonNaturalStoneRemainsVanilla(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setStoneRockHarvesting(helper, true);
        breakBlock(
                helper,
                Blocks.STONE_BRICKS,
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        helper.assertItemEntityCountIsAtLeast(
                Items.STONE_BRICKS,
                BLOCK_POS,
                2.0,
                1
        );
        helper.assertItemEntityNotPresent(
                ModItems.ROCK.get(),
                BLOCK_POS,
                2.0
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Primitive harvest rules have server opt-outs")
    static void harvestConfigOptOutsRestoreVanilla(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setKnifePlantHarvesting(helper, false);
        ConfigFixture.setStoneRockHarvesting(helper, false);
        breakBlock(
                helper,
                Blocks.FERN,
                ModItems.FLINT_KNIFE.get().getDefaultInstance()
        );
        helper.assertItemEntityNotPresent(
                ModItems.PLANT_FIBER.get(),
                BLOCK_POS,
                2.0
        );

        breakBlock(helper, Blocks.STONE, new ItemStack(Items.WOODEN_PICKAXE));
        helper.assertItemEntityCountIsAtLeast(
                Items.COBBLESTONE,
                BLOCK_POS,
                2.0,
                1
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Field plant harvesting costs one knife durability")
    static void plantHarvestDamagesKnifeOnce(ExtendedGameTestHelper helper) {
        ConfigFixture.setKnifePlantHarvesting(helper, true);
        ItemStack knife = ModItems.FLINT_KNIFE.get().getDefaultInstance();
        breakBlock(helper, Blocks.FERN, knife);
        helper.assertTrue(
                knife.getDamageValue() == 1,
                "Knife took " + knife.getDamageValue()
                        + " damage instead of one"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The registry never upgrades another mod's denial")
    static void registryPreservesHarvestDenial(
            ExtendedGameTestHelper helper
    ) {
        HarvestContext context = HarvestContext.permissionCheck(
                false,
                Blocks.OAK_LOG.defaultBlockState(),
                ModItems.FLINT_HATCHET.get().getDefaultInstance()
        );
        HarvestRuleRegistry.defaults().evaluate(context);
        helper.assertFalse(
                context.canHarvest(),
                "Ordered rules upgraded an existing harvest denial"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Harvest configuration defaults are versioned")
    static void harvestConfigDefaultsAreVersioned(
            ExtendedGameTestHelper helper
    ) {
        helper.assertTrue(
                MaterialProgressionConfig.configVersion() == 1,
                "Unexpected harvest configuration version"
        );
        helper.assertTrue(
                MaterialProgressionConfig.requireAxeForLogs(),
                "Log gating did not default on"
        );
        helper.assertTrue(
                MaterialProgressionConfig.knifePlantHarvesting(),
                "Knife harvesting did not default on"
        );
        helper.assertTrue(
                MaterialProgressionConfig.stoneRockHarvesting(),
                "Stone Rock harvesting did not default on"
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
