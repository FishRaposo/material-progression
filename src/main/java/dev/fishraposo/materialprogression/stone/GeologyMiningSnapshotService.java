package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Resolves one validated request entirely from authoritative server state.
 */
public final class GeologyMiningSnapshotService {
    private static final double INTERACTION_DISTANCE_BUFFER = 1.0D;

    private GeologyMiningSnapshotService() {
    }

    public static Optional<GeologyMiningSnapshotPayload> resolve(
            ServerPlayer player,
            GeologyMiningSnapshotRequest request
    ) {
        ServerLevel level = player.level();
        GeologyMiningTarget requested = request.target();
        if (!level.dimension().identifier().equals(requested.dimension())
                || !player.isWithinBlockInteractionRange(
                        requested.pos(),
                        INTERACTION_DISTANCE_BUFFER
                )
                || !level.hasChunkAt(requested.pos())) {
            return Optional.empty();
        }

        BlockState actualState = level.getBlockState(requested.pos());
        GeologyMiningTarget actualTarget = new GeologyMiningTarget(
                level.dimension().identifier(),
                requested.pos(),
                Block.getId(actualState)
        );
        boolean enabled =
                MaterialProgressionConfig.enableGeologicalHardness();
        Optional<GeologyTier> tier = enabled
                ? GeologyTierResolver.resolve(
                        level,
                        requested.pos(),
                        actualState
                )
                : Optional.empty();
        GeologyMiningSnapshotRequest actualRequest =
                new GeologyMiningSnapshotRequest(
                        request.requestId(),
                        actualTarget
                );
        return Optional.of(GeologyMiningSnapshotPayload.resolved(
                actualRequest,
                enabled,
                tier,
                StoneFamilyCatalog.version(),
                GeologyDimensionProfileCatalog.version()
        ));
    }
}
