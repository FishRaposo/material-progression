package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.GeologyTierResolver;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneTracker;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneResistance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GeologyTierGameTests {
    private static final BlockPos SUPPORT = new BlockPos(1, 1, 1);
    private static final ResourceKey<Level> OTHER_DIMENSION =
            ResourceKey.create(
                    Registries.DIMENSION,
                    Identifier.fromNamespaceAndPath(
                            MaterialProgressionGameTestMod.MOD_ID,
                            "other"
                    )
            );

    private GeologyTierGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Overworld geology uses every exact depth boundary")
    static void overworldDepthBoundaries(ExtendedGameTestHelper helper) {
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, 49, false, 0);
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, 48, false, 1);
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, 17, false, 1);
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, 16, false, 2);
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, -15, false, 2);
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, -16, false, 3);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Nether geology uses every exact depth boundary")
    static void netherDepthBoundaries(ExtendedGameTestHelper helper) {
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 96, false, 0);
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 95, false, 1);
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 64, false, 1);
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 63, false, 2);
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 32, false, 2);
        assertNaturalTier(helper, Level.NETHER, StoneFamily.NETHERRACK, 31, false, 3);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "All End raw families start at level two before exposure")
    static void endAndOtherDimensionFallbacks(ExtendedGameTestHelper helper) {
        assertNaturalTier(helper, Level.END, StoneFamily.END_STONE, 64, false, 2);
        assertNaturalTier(helper, Level.END, StoneFamily.STONE, 64, false, 2);
        assertNaturalTier(helper, Level.END, StoneFamily.STONE, 64, true, 1);
        assertNaturalTier(helper, OTHER_DIMENSION, StoneFamily.END_STONE, -64, false, 0);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Soft and hard families shift a standard layer by one")
    static void familyResistanceShifts(ExtendedGameTestHelper helper) {
        assertNaturalTier(
                helper,
                Level.OVERWORLD,
                StoneFamily.CALCITE,
                0,
                false,
                StoneResistance.SOFT,
                1
        );
        assertNaturalTier(
                helper,
                Level.OVERWORLD,
                StoneFamily.STONE,
                0,
                false,
                StoneResistance.STANDARD,
                2
        );
        assertNaturalTier(
                helper,
                Level.OVERWORLD,
                StoneFamily.DEEPSLATE,
                0,
                false,
                StoneResistance.HARD,
                3
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Any exposed face reduces geology by exactly one level")
    static void exposureReducesAtMostOneLevel(ExtendedGameTestHelper helper) {
        assertNaturalTier(helper, Level.OVERWORLD, StoneFamily.STONE, -16, true, 2);
        assertNaturalTier(
                helper,
                Level.OVERWORLD,
                StoneFamily.CALCITE,
                64,
                true,
                StoneResistance.SOFT,
                0
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Non-family blocks are outside geological resistance")
    static void nonFamilyBlocksAreUnaffected(ExtendedGameTestHelper helper) {
        helper.setBlock(SUPPORT, Blocks.DIRT);
        helper.assertTrue(
                GeologyTierResolver.resolve(
                        helper.getLevel(),
                        helper.absolutePos(SUPPORT),
                        helper.getBlockState(SUPPORT)
                ).isEmpty(),
                "dirt unexpectedly resolved a geology tier"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Player-placed raw stone is tracked and always level zero")
    static void playerPlacedRawStoneIsLevelZero(ExtendedGameTestHelper helper) {
        helper.setBlock(SUPPORT, Blocks.COBBLESTONE);
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        ItemStack stone = new ItemStack(Blocks.STONE);
        player.setItemInHand(InteractionHand.MAIN_HAND, stone);
        helper.placeAt(player, stone, SUPPORT, net.minecraft.core.Direction.UP);

        BlockPos placed = SUPPORT.above();
        BlockPos absolute = helper.absolutePos(placed);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(
                    PlacedRawStoneTracker.isMarked(helper.getLevel(), absolute),
                    "player placement did not create a raw-stone marker"
            );
            helper.assertValueEqual(
                    0,
                    GeologyTierResolver.resolve(
                            helper.getLevel(),
                            absolute,
                            helper.getBlockState(placed)
                    ).orElseThrow().level(),
                    "placed raw-stone geology level"
            );
            helper.succeed();
        });
    }

    private static void assertNaturalTier(
            ExtendedGameTestHelper helper,
            ResourceKey<Level> dimension,
            StoneFamily family,
            int y,
            boolean exposed,
            int expected
    ) {
        assertNaturalTier(
                helper,
                dimension,
                family,
                y,
                exposed,
                StoneResistance.STANDARD,
                expected
        );
    }

    private static void assertNaturalTier(
            ExtendedGameTestHelper helper,
            ResourceKey<Level> dimension,
            StoneFamily family,
            int y,
            boolean exposed,
            StoneResistance resistance,
            int expected
    ) {
        GeologyTier actual = GeologyTierResolver.naturalTier(
                dimension,
                family,
                resistance,
                y,
                exposed
        );
        helper.assertValueEqual(expected, actual.level(), "geology level at y=" + y);
    }
}
