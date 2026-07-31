package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-authoritative geology result for one exact client mining target.
 */
public record GeologyMiningSnapshotPayload(
        long requestId,
        GeologyMiningTarget target,
        boolean hardnessEnabled,
        int tierLevel,
        long stoneFamilyCatalogVersion,
        long dimensionProfileCatalogVersion
) implements CustomPacketPayload {
    public static final int NO_TIER = -1;
    public static final Type<GeologyMiningSnapshotPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "geology_mining_snapshot"
            )
    );
    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            GeologyMiningSnapshotPayload
            > STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    GeologyMiningSnapshotPayload::requestId,
                    GeologyMiningTarget.STREAM_CODEC,
                    GeologyMiningSnapshotPayload::target,
                    ByteBufCodecs.BOOL,
                    GeologyMiningSnapshotPayload::hardnessEnabled,
                    ByteBufCodecs.VAR_INT,
                    GeologyMiningSnapshotPayload::encodedTier,
                    ByteBufCodecs.VAR_LONG,
                    GeologyMiningSnapshotPayload::stoneFamilyCatalogVersion,
                    ByteBufCodecs.VAR_LONG,
                    GeologyMiningSnapshotPayload
                            ::dimensionProfileCatalogVersion,
                    GeologyMiningSnapshotPayload::decode
            );

    public GeologyMiningSnapshotPayload {
        if (requestId <= 0L) {
            throw new IllegalArgumentException(
                    "requestId must be positive"
            );
        }
        Objects.requireNonNull(target, "target");
        if (tierLevel < NO_TIER
                || tierLevel > GeologyTier.LEVEL_3.level()) {
            throw new IllegalArgumentException(
                    "tierLevel must be from -1 through 3"
            );
        }
        if (stoneFamilyCatalogVersion < 0L
                || dimensionProfileCatalogVersion < 0L) {
            throw new IllegalArgumentException(
                    "catalog versions must be non-negative"
            );
        }
        if (!hardnessEnabled && tierLevel != NO_TIER) {
            throw new IllegalArgumentException(
                    "disabled hardness may not carry a geology tier"
            );
        }
    }

    public static GeologyMiningSnapshotPayload resolved(
            GeologyMiningSnapshotRequest request,
            boolean hardnessEnabled,
            Optional<GeologyTier> tier,
            long stoneFamilyCatalogVersion,
            long dimensionProfileCatalogVersion
    ) {
        return new GeologyMiningSnapshotPayload(
                request.requestId(),
                request.target(),
                hardnessEnabled,
                hardnessEnabled && tier.isPresent()
                        ? tier.orElseThrow().level()
                        : NO_TIER,
                stoneFamilyCatalogVersion,
                dimensionProfileCatalogVersion
        );
    }

    public float speedDivisor() {
        return hardnessEnabled && tierLevel != NO_TIER
                ? GeologyTier.values()[tierLevel].speedDivisor()
                : 1.0F;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private int encodedTier() {
        return tierLevel + 1;
    }

    private static GeologyMiningSnapshotPayload decode(
            long requestId,
            GeologyMiningTarget target,
            boolean hardnessEnabled,
            int encodedTier,
            long stoneFamilyCatalogVersion,
            long dimensionProfileCatalogVersion
    ) {
        if (encodedTier < 0
                || encodedTier > GeologyTier.LEVEL_3.level() + 1) {
            throw new IllegalArgumentException(
                    "encoded tier must be from 0 through 4"
            );
        }
        return new GeologyMiningSnapshotPayload(
                requestId,
                target,
                hardnessEnabled,
                encodedTier - 1,
                stoneFamilyCatalogVersion,
                dimensionProfileCatalogVersion
        );
    }
}
