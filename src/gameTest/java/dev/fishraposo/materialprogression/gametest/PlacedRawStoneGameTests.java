package dev.fishraposo.materialprogression.gametest;

import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneMarkers;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class PlacedRawStoneGameTests {
    private static final BlockPos ROOT = new BlockPos(1, 1, 1);

    private PlacedRawStoneGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Placed raw-stone markers survive their persistent codec")
    static void markerCodecRoundTrips(ExtendedGameTestHelper helper) {
        PlacedRawStoneMarkers original = new PlacedRawStoneMarkers();
        BlockPos negative = new BlockPos(1, -64, 15);
        BlockPos high = new BlockPos(15, 320, 0);
        original.add(negative);
        original.add(high);

        var encoded = PlacedRawStoneMarkers.CODEC.codec()
                .encodeStart(JsonOps.INSTANCE, original)
                .getOrThrow();
        PlacedRawStoneMarkers decoded = PlacedRawStoneMarkers.CODEC.codec()
                .parse(JsonOps.INSTANCE, encoded)
                .getOrThrow();

        helper.assertTrue(decoded.contains(negative), "negative-Y marker was lost");
        helper.assertTrue(decoded.contains(high), "high marker was lost");
        helper.assertFalse(
                decoded.contains(new BlockPos(0, -64, 15)),
                "codec introduced an unrelated marker"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 80)
    @EmptyTemplate
    @TestHolder(description = "Piston extension transfers a placed raw-stone marker")
    static void pistonExtensionTransfersMarker(ExtendedGameTestHelper helper) {
        BlockPos piston = ROOT.west();
        BlockPos source = ROOT;
        BlockPos destination = ROOT.east();
        helper.setBlock(
                piston,
                Blocks.PISTON.defaultBlockState().setValue(
                        PistonBaseBlock.FACING,
                        helper.getAbsoluteDirection(Direction.EAST)
                )
        );
        helper.setBlock(source, Blocks.GRANITE);
        PlacedRawStoneTracker.mark(helper.getLevel(), helper.absolutePos(source));

        helper.runAfterDelay(1, () -> {
            helper.setBlock(piston.above(), Blocks.REDSTONE_BLOCK);
            helper.getLevel().neighborChanged(
                    helper.absolutePos(piston),
                    Blocks.REDSTONE_BLOCK,
                    null
            );
        });
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.GRANITE, destination);
            helper.assertFalse(
                    PlacedRawStoneTracker.isMarked(
                            helper.getLevel(),
                            helper.absolutePos(source)
                    ),
                    "piston source retained its marker"
            );
            helper.assertTrue(
                    PlacedRawStoneTracker.isMarked(
                            helper.getLevel(),
                            helper.absolutePos(destination)
                    ),
                    "piston destination did not inherit its marker"
            );
        });
    }

    @GameTest(timeoutTicks = 80)
    @EmptyTemplate
    @TestHolder(description = "A piston transfers raw stone placed in the same tick")
    static void sameTickPistonTransfersPlayerPlacement(
            ExtendedGameTestHelper helper
    ) {
        BlockPos piston = ROOT.west();
        BlockPos support = ROOT.below();
        BlockPos destination = ROOT.east();
        helper.setBlock(
                piston,
                Blocks.PISTON.defaultBlockState().setValue(
                        PistonBaseBlock.FACING,
                        helper.getAbsoluteDirection(Direction.EAST)
                )
        );
        helper.setBlock(support, Blocks.COBBLESTONE);
        placeStone(helper, player(helper, GameType.SURVIVAL), support);

        helper.setBlock(piston.above(), Blocks.REDSTONE_BLOCK);
        helper.getLevel().neighborChanged(
                helper.absolutePos(piston),
                Blocks.REDSTONE_BLOCK,
                null
        );

        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.STONE, destination);
            helper.assertTrue(
                    PlacedRawStoneTracker.isMarked(
                            helper.getLevel(),
                            helper.absolutePos(destination)
                    ),
                    "same-tick piston destination did not inherit the marker"
            );
        });
    }

    @GameTest(timeoutTicks = 100)
    @EmptyTemplate
    @TestHolder(description = "Sticky-piston retraction transfers a placed raw-stone marker")
    static void stickyRetractionTransfersMarker(ExtendedGameTestHelper helper) {
        BlockPos piston = ROOT.west();
        BlockPos destination = ROOT;
        BlockPos source = ROOT.east();
        helper.setBlock(
                piston,
                Blocks.STICKY_PISTON.defaultBlockState().setValue(
                        PistonBaseBlock.FACING,
                        helper.getAbsoluteDirection(Direction.EAST)
                )
        );
        helper.setBlock(piston.above(), Blocks.REDSTONE_BLOCK);
        helper.getLevel().neighborChanged(
                helper.absolutePos(piston),
                Blocks.REDSTONE_BLOCK,
                null
        );

        helper.runAfterDelay(4, () -> {
            helper.assertBlockProperty(piston, PistonBaseBlock.EXTENDED, true);
            helper.setBlock(source, Blocks.GRANITE);
            PlacedRawStoneTracker.mark(
                    helper.getLevel(),
                    helper.absolutePos(source)
            );
            helper.setBlock(piston.above(), Blocks.AIR);
            helper.getLevel().neighborChanged(
                    helper.absolutePos(piston),
                    Blocks.AIR,
                    null
            );
        });
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.GRANITE, destination);
            helper.assertFalse(
                    PlacedRawStoneTracker.isMarked(
                            helper.getLevel(),
                            helper.absolutePos(source)
                    ),
                    "sticky source retained its marker"
            );
            helper.assertTrue(
                    PlacedRawStoneTracker.isMarked(
                            helper.getLevel(),
                            helper.absolutePos(destination)
                    ),
                    "sticky destination did not inherit its marker"
            );
        });
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Creative removal clears the stored placed-stone marker without loot")
    static void creativeRemovalClearsStoredMarker(
            ExtendedGameTestHelper helper
    ) {
        BlockPos support = ROOT.below();
        helper.setBlock(support, Blocks.COBBLESTONE);
        ServerPlayer player = player(helper, GameType.CREATIVE);
        placeStone(helper, player, support);
        BlockPos absolute = helper.absolutePos(ROOT);

        helper.runAfterDelay(1, () -> {
            helper.assertTrue(
                    hasStoredMarker(helper, absolute),
                    "creative fixture was not persistently marked"
            );
            helper.assertTrue(
                    player.gameMode.destroyBlock(absolute),
                    "creative removal was rejected"
            );
            helper.assertBlockPresent(Blocks.AIR, ROOT);
        });
        helper.runAfterDelay(2, () -> {
            helper.assertFalse(
                    hasStoredMarker(helper, absolute),
                    "creative removal retained the stored marker"
            );
            helper.succeed();
        });
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Incorrect-tool removal clears the stored marker even without harvest")
    static void incorrectToolRemovalClearsStoredMarker(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        BlockPos support = ROOT.below();
        helper.setBlock(support, Blocks.COBBLESTONE);
        ServerPlayer player = player(helper, GameType.SURVIVAL);
        placeStone(helper, player, support);
        BlockPos absolute = helper.absolutePos(ROOT);

        helper.runAfterDelay(1, () -> {
            helper.assertTrue(
                    hasStoredMarker(helper, absolute),
                    "incorrect-tool fixture was not persistently marked"
            );
            player.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    new ItemStack(Items.IRON_SWORD)
            );
            helper.assertTrue(
                    player.gameMode.destroyBlock(absolute),
                    "incorrect-tool removal was rejected"
            );
            helper.assertBlockPresent(Blocks.AIR, ROOT);
        });
        helper.runAfterDelay(2, () -> {
            helper.assertFalse(
                    hasStoredMarker(helper, absolute),
                    "incorrect-tool removal retained the stored marker"
            );
            helper.succeed();
        });
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Remove and identical replacement cannot retain an old marker")
    static void sameStateReplacementClearsOldMarker(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, Blocks.STONE);
        BlockPos absolute = helper.absolutePos(ROOT);
        PlacedRawStoneTracker.mark(helper.getLevel(), absolute);
        helper.assertTrue(
                hasStoredMarker(helper, absolute),
                "ABA fixture did not start with a stored marker"
        );
        ServerPlayer player = player(helper, GameType.CREATIVE);

        helper.assertTrue(
                player.gameMode.destroyBlock(absolute),
                "creative ABA removal was rejected"
        );
        helper.assertBlockPresent(Blocks.AIR, ROOT);
        helper.setBlock(ROOT, Blocks.STONE);

        helper.runAfterDelay(1, () -> {
            helper.assertBlockPresent(Blocks.STONE, ROOT);
            helper.assertFalse(
                    hasStoredMarker(helper, absolute),
                    "identical replacement retained the removed marker"
            );
            helper.succeed();
        });
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Lower-priority placement cancellation never creates a marker")
    static void canceledPlacementDoesNotCreateMarker(
            ExtendedGameTestHelper helper
    ) {
        BlockPos support = ROOT.below();
        helper.setBlock(support, Blocks.COBBLESTONE);
        ServerPlayer player = player(helper, GameType.SURVIVAL);
        BlockPos absolute = helper.absolutePos(ROOT);
        MaterialProgressionGameTestMod.cancelNextPlacementAt(absolute);
        helper.addEndListener(ignored ->
                MaterialProgressionGameTestMod.clearCancellations()
        );

        placeStone(helper, player, support);
        helper.assertBlockPresent(Blocks.AIR, ROOT);
        helper.assertFalse(
                PlacedRawStoneTracker.isMarked(helper.getLevel(), absolute),
                "canceled placement remained immediately classified as placed"
        );
        helper.assertFalse(
                hasStoredMarker(helper, absolute),
                "canceled placement immediately created a stored marker"
        );
        helper.runAfterDelay(1, () -> {
            helper.assertFalse(
                    hasStoredMarker(helper, absolute),
                    "canceled placement created a stored marker"
            );
            helper.succeed();
        });
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Lower-priority living-destruction cancellation retains a marker")
    static void canceledLivingDestructionRetainsMarker(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        BlockPos absolute = helper.absolutePos(ROOT);
        PlacedRawStoneTracker.mark(helper.getLevel(), absolute);
        MaterialProgressionGameTestMod.cancelNextLivingDestructionAt(absolute);
        helper.addEndListener(ignored ->
                MaterialProgressionGameTestMod.clearCancellations()
        );

        var event = NeoForge.EVENT_BUS.post(new LivingDestroyBlockEvent(
                player(helper, GameType.SURVIVAL),
                absolute,
                helper.getBlockState(ROOT)
        ));
        helper.assertTrue(event.isCanceled(), "destruction fixture was not canceled");
        helper.assertBlockPresent(Blocks.GRANITE, ROOT);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(
                    hasStoredMarker(helper, absolute),
                    "canceled destruction cleared the stored marker"
            );
            helper.succeed();
        });
    }

    private static void placeStone(
            ExtendedGameTestHelper helper,
            ServerPlayer player,
            BlockPos support
    ) {
        ItemStack stone = new ItemStack(Blocks.STONE);
        player.setItemInHand(InteractionHand.MAIN_HAND, stone);
        helper.placeAt(player, stone, support, Direction.UP);
    }

    private static ServerPlayer player(
            ExtendedGameTestHelper helper,
            GameType gameType
    ) {
        return helper.makeTickingMockServerPlayerInLevel(gameType);
    }

    private static boolean hasStoredMarker(
            ExtendedGameTestHelper helper,
            BlockPos absolute
    ) {
        PlacedRawStoneMarkers markers = helper.getLevel()
                .getChunkAt(absolute)
                .getExistingDataOrNull(ModDataAttachments.PLACED_RAW_STONES);
        return markers != null && markers.contains(absolute);
    }
}
