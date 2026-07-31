package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.stone.GeologyMiningPredictionCache;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotPayload;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotRequest;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotService;
import dev.fishraposo.materialprogression.stone.GeologyMiningTarget;
import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneTracker;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GeologyMiningSyncGameTests {
    private static final BlockPos TARGET = new BlockPos(1, 1, 1);

    private GeologyMiningSyncGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(
            description = "Client prediction applies every authoritative tier exactly once"
    )
    static void cacheAppliesExactTierDivisors(
            ExtendedGameTestHelper helper
    ) {
        float originalSpeed = 12.0F;
        float[] expected = {12.0F, 4.8F, 3.0F, 2.0F};

        for (GeologyTier tier : GeologyTier.values()) {
            GeologyMiningPredictionCache cache =
                    new GeologyMiningPredictionCache();
            GeologyMiningTarget target = target(
                    Level.OVERWORLD.identifier(),
                    TARGET,
                    Blocks.STONE
            );
            GeologyMiningSnapshotRequest request =
                    cache.beginTarget(target, true, 100L);
            helper.assertTrue(
                    cache.accept(
                            GeologyMiningSnapshotPayload.resolved(
                                    request,
                                    true,
                                    Optional.of(tier),
                                    7L,
                                    9L
                            ),
                            101L
                    ),
                    "matching tier " + tier.level() + " snapshot was rejected"
            );
            helper.assertValueEqual(
                    expected[tier.level()],
                    cache.adjustSpeed(
                            originalSpeed,
                            target,
                            true,
                            101L
                    ),
                    "predicted speed for tier " + tier.level()
            );
        }

        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(
            description = "Client prediction rejects stale targets and expires safely"
    )
    static void cacheRejectsMismatchExpiryAndOutOfOrder(
            ExtendedGameTestHelper helper
    ) {
        GeologyMiningPredictionCache cache =
                new GeologyMiningPredictionCache();
        GeologyMiningTarget first = target(
                Level.OVERWORLD.identifier(),
                TARGET,
                Blocks.STONE
        );
        GeologyMiningTarget second = target(
                Level.OVERWORLD.identifier(),
                TARGET.east(),
                Blocks.DEEPSLATE
        );
        GeologyMiningSnapshotRequest firstRequest =
                cache.beginTarget(first, true, 10L);
        helper.assertValueEqual(
                2.0F,
                cache.adjustSpeed(12.0F, first, true, 10L),
                "proven-family pending speed"
        );
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, first, false, 10L),
                "non-family pending speed"
        );

        GeologyMiningSnapshotRequest secondRequest =
                cache.beginTarget(second, true, 11L);
        helper.assertTrue(
                !cache.accept(
                        GeologyMiningSnapshotPayload.resolved(
                                firstRequest,
                                true,
                                Optional.of(GeologyTier.LEVEL_3),
                                1L,
                                1L
                        ),
                        12L
                ),
                "out-of-order first-target response was accepted"
        );
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, first, true, 12L),
                "old target remained active"
        );
        GeologyMiningSnapshotRequest afterTargetMismatch =
                cache.continueTarget(second, true, 12L).orElseThrow();
        helper.assertTrue(
                afterTargetMismatch.requestId() > secondRequest.requestId(),
                "old target mismatch did not start a fresh request generation"
        );
        secondRequest = afterTargetMismatch;

        GeologyMiningTarget wrongState = target(
                Level.OVERWORLD.identifier(),
                TARGET.east(),
                Blocks.DIRT
        );
        helper.assertTrue(
                !cache.accept(
                        new GeologyMiningSnapshotPayload(
                                secondRequest.requestId(),
                                wrongState,
                                true,
                                GeologyTier.LEVEL_2.level(),
                                2L,
                                2L
                        ),
                        12L
                ),
                "block-state-mismatched response was accepted"
        );
        helper.assertTrue(
                cache.accept(
                        GeologyMiningSnapshotPayload.resolved(
                                secondRequest,
                                true,
                                Optional.of(GeologyTier.LEVEL_2),
                                2L,
                                2L
                        ),
                        12L
                ),
                "current response was rejected"
        );
        helper.assertValueEqual(
                3.0F,
                cache.adjustSpeed(12.0F, second, true, 12L),
                "current exact response speed"
        );
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, second, false, 12L),
                "non-candidate current target used an authoritative divisor"
        );

        long expiredAt = 12L
                + GeologyMiningPredictionCache.SNAPSHOT_TTL_TICKS;
        helper.assertValueEqual(
                2.0F,
                cache.adjustSpeed(12.0F, second, true, expiredAt),
                "expired candidate snapshot did not return to conservative speed"
        );
        helper.assertTrue(
                !cache.accept(
                        GeologyMiningSnapshotPayload.resolved(
                                secondRequest,
                                true,
                                Optional.of(GeologyTier.LEVEL_1),
                                3L,
                                3L
                        ),
                        expiredAt
                ),
                "response arriving after the request TTL was accepted"
        );
        GeologyMiningSnapshotRequest refresh = cache.continueTarget(
                second,
                true,
                expiredAt
        ).orElseThrow();
        helper.assertTrue(
                refresh.requestId() > secondRequest.requestId(),
                "refresh did not advance the request generation"
        );
        helper.assertTrue(
                !cache.accept(
                        GeologyMiningSnapshotPayload.resolved(
                                secondRequest,
                                true,
                                Optional.of(GeologyTier.LEVEL_1),
                                3L,
                                3L
                        ),
                        expiredAt
                ),
                "pre-refresh response was accepted"
        );

        GeologyMiningTarget changedState = target(
                Level.OVERWORLD.identifier(),
                TARGET.east(),
                Blocks.DIRT
        );
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, changedState, true, expiredAt),
                "block-state-mismatched target used a cached divisor"
        );
        GeologyMiningSnapshotRequest afterStateMismatch =
                cache.continueTarget(second, true, expiredAt).orElseThrow();
        helper.assertTrue(
                afterStateMismatch.requestId() > refresh.requestId(),
                "block-state mismatch did not start a fresh request generation"
        );

        GeologyMiningTarget otherDimension = target(
                Level.NETHER.identifier(),
                TARGET.east(),
                Blocks.DEEPSLATE
        );
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, otherDimension, true, expiredAt),
                "dimension-mismatched target used a cached divisor"
        );
        GeologyMiningSnapshotRequest afterDimensionMismatch =
                cache.continueTarget(second, true, expiredAt).orElseThrow();
        helper.assertTrue(
                afterDimensionMismatch.requestId()
                        > afterStateMismatch.requestId(),
                "dimension mismatch did not start a fresh request generation"
        );
        cache.clear();
        helper.assertValueEqual(
                12.0F,
                cache.adjustSpeed(12.0F, second, true, expiredAt),
                "cleared cache still changed speed"
        );
        GeologyMiningSnapshotRequest afterDisconnectClear =
                cache.continueTarget(second, true, expiredAt).orElseThrow();
        helper.assertTrue(
                afterDisconnectClear.requestId()
                        > afterDimensionMismatch.requestId(),
                "disconnect clear did not start a fresh request generation"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(
            description = "Server snapshots preserve placed level zero and the hardness toggle"
    )
    static void serverSnapshotUsesPlacedMarkerAndToggle(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        helper.setBlock(TARGET, Blocks.STONE);
        BlockPos absolute = helper.absolutePos(TARGET);
        PlacedRawStoneTracker.mark(helper.getLevel(), absolute);
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        player.setPos(
                absolute.getX() + 0.5D,
                absolute.getY() + 0.5D,
                absolute.getZ() + 0.5D
        );
        GeologyMiningSnapshotRequest request = new GeologyMiningSnapshotRequest(
                1L,
                target(
                        helper.getLevel().dimension().identifier(),
                        absolute,
                        Blocks.STONE
                )
        );
        GeologyMiningSnapshotPayload placed =
                GeologyMiningSnapshotService.resolve(player, request)
                        .orElseThrow();
        helper.assertTrue(placed.hardnessEnabled(), "hardness was not enabled");
        helper.assertValueEqual(
                GeologyTier.LEVEL_0.level(),
                placed.tierLevel(),
                "placed raw stone snapshot tier"
        );

        ConfigFixture.setEnableGeologicalHardness(helper, false);
        GeologyMiningSnapshotPayload disabled =
                GeologyMiningSnapshotService.resolve(player, request)
                        .orElseThrow();
        helper.assertTrue(
                !disabled.hardnessEnabled(),
                "disabled hardness was reported enabled"
        );
        helper.assertValueEqual(
                GeologyMiningSnapshotPayload.NO_TIER,
                disabled.tierLevel(),
                "disabled hardness carried a tier"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(
            description = "Server snapshots use actual state and external family resolution"
    )
    static void serverSnapshotUsesActualExternalState(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        helper.setBlock(
                TARGET,
                MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get()
        );
        BlockPos absolute = helper.absolutePos(TARGET);
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        player.setPos(
                absolute.getX() + 0.5D,
                absolute.getY() + 0.5D,
                absolute.getZ() + 0.5D
        );
        GeologyMiningSnapshotRequest staleRequest =
                new GeologyMiningSnapshotRequest(
                        2L,
                        target(
                                helper.getLevel().dimension().identifier(),
                                absolute,
                                Blocks.DIRT
                        )
        );
        GeologyMiningSnapshotPayload actual =
                GeologyMiningSnapshotService.resolve(player, staleRequest)
                        .orElseThrow();
        helper.assertValueEqual(
                Block.getId(
                        MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE
                                .get()
                                .defaultBlockState()
                ),
                actual.target().blockStateId(),
                "authoritative external block-state ID"
        );
        helper.assertTrue(
                actual.tierLevel() >= GeologyTier.LEVEL_0.level(),
                "external family did not resolve an authoritative tier"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Mining snapshot payload codecs round-trip bounded data")
    static void snapshotCodecsRoundTrip(ExtendedGameTestHelper helper) {
        GeologyMiningTarget target = target(
                Level.OVERWORLD.identifier(),
                TARGET,
                Blocks.STONE
        );
        GeologyMiningSnapshotRequest request =
                new GeologyMiningSnapshotRequest(42L, target);
        GeologyMiningSnapshotPayload payload =
                GeologyMiningSnapshotPayload.resolved(
                        request,
                        true,
                        Optional.of(GeologyTier.LEVEL_3),
                        12L,
                        15L
                );

        RegistryFriendlyByteBuf requestBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                helper.getLevel().registryAccess()
        );
        RegistryFriendlyByteBuf payloadBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                helper.getLevel().registryAccess()
        );
        try {
            GeologyMiningSnapshotRequest.STREAM_CODEC.encode(
                    requestBuffer,
                    request
            );
            helper.assertValueEqual(
                    request,
                    GeologyMiningSnapshotRequest.STREAM_CODEC.decode(
                            requestBuffer
                    ),
                    "snapshot request codec"
            );
            GeologyMiningSnapshotPayload.STREAM_CODEC.encode(
                    payloadBuffer,
                    payload
            );
            helper.assertValueEqual(
                    payload,
                    GeologyMiningSnapshotPayload.STREAM_CODEC.decode(
                            payloadBuffer
                    ),
                    "snapshot response codec"
            );
        } finally {
            requestBuffer.release();
            payloadBuffer.release();
        }

        assertRejected(
                helper,
                () -> new GeologyMiningSnapshotRequest(0L, target),
                "zero request ID"
        );
        assertRejected(
                helper,
                () -> new GeologyMiningTarget(
                        Level.OVERWORLD.identifier(),
                        TARGET,
                        -1
                ),
                "negative block-state ID"
        );
        assertRejected(
                helper,
                () -> new GeologyMiningSnapshotPayload(
                        1L,
                        target,
                        true,
                        4,
                        0L,
                        0L
                ),
                "out-of-range tier"
        );
        helper.succeed();
    }

    private static GeologyMiningTarget target(
            Identifier dimension,
            BlockPos pos,
            Block block
    ) {
        return new GeologyMiningTarget(
                dimension,
                pos,
                Block.getId(block.defaultBlockState())
        );
    }

    private static void assertRejected(
            ExtendedGameTestHelper helper,
            Runnable operation,
            String context
    ) {
        boolean rejected = false;
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        helper.assertTrue(rejected, context + " was accepted");
    }
}
