package dev.fishraposo.materialprogression.stone;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * The exact client-visible block identity associated with one mining request.
 */
public record GeologyMiningTarget(
        Identifier dimension,
        BlockPos pos,
        int blockStateId
) {
    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            GeologyMiningTarget
            > STREAM_CODEC = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    GeologyMiningTarget::dimension,
                    BlockPos.STREAM_CODEC,
                    GeologyMiningTarget::pos,
                    ByteBufCodecs.VAR_INT,
                    GeologyMiningTarget::blockStateId,
                    GeologyMiningTarget::new
            );

    public GeologyMiningTarget {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(pos, "pos");
        if (blockStateId < 0) {
            throw new IllegalArgumentException(
                    "blockStateId must be non-negative"
            );
        }
        pos = pos.immutable();
    }
}
