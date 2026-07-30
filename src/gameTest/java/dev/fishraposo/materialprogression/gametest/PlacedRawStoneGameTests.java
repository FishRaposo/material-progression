package dev.fishraposo.materialprogression.gametest;

import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneMarkers;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
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
}
