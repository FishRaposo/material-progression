package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * One client mining-target generation requesting an authoritative server tier.
 */
public record GeologyMiningSnapshotRequest(
        long requestId,
        GeologyMiningTarget target
) implements CustomPacketPayload {
    public static final Type<GeologyMiningSnapshotRequest> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "geology_mining_snapshot_request"
            )
    );
    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            GeologyMiningSnapshotRequest
            > STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    GeologyMiningSnapshotRequest::requestId,
                    GeologyMiningTarget.STREAM_CODEC,
                    GeologyMiningSnapshotRequest::target,
                    GeologyMiningSnapshotRequest::new
            );

    public GeologyMiningSnapshotRequest {
        if (requestId <= 0L) {
            throw new IllegalArgumentException(
                    "requestId must be positive"
            );
        }
        Objects.requireNonNull(target, "target");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
