package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.transaction.OperationPreview;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WorkshopPreviewPayload(
        int containerId,
        long sequence,
        Identifier recipeId,
        int requested,
        int executable,
        PreviewStack consumed,
        PreviewStack produced,
        int durabilityCost,
        List<PreviewStack> remainders,
        long revision,
        String failure
) implements CustomPacketPayload {
    private static final int MAX_REMAINDERS = 16;
    private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    Identifier::parse,
                    Identifier::toString
            );
    public static final Type<WorkshopPreviewPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "workshop_preview"
            )
    );
    public static final StreamCodec<ByteBuf, WorkshopPreviewPayload>
            STREAM_CODEC = new StreamCodec<>() {
                @Override
                public WorkshopPreviewPayload decode(ByteBuf buffer) {
                    int containerId = ByteBufCodecs.VAR_INT.decode(buffer);
                    long sequence = ByteBufCodecs.VAR_LONG.decode(buffer);
                    Identifier recipeId = IDENTIFIER_CODEC.decode(buffer);
                    int requested = ByteBufCodecs.VAR_INT.decode(buffer);
                    int executable = ByteBufCodecs.VAR_INT.decode(buffer);
                    PreviewStack consumed =
                            PreviewStack.STREAM_CODEC.decode(buffer);
                    PreviewStack produced =
                            PreviewStack.STREAM_CODEC.decode(buffer);
                    int durability =
                            ByteBufCodecs.VAR_INT.decode(buffer);
                    int remainderCount =
                            ByteBufCodecs.VAR_INT.decode(buffer);
                    if (remainderCount < 0
                            || remainderCount > MAX_REMAINDERS) {
                        throw new IllegalArgumentException(
                                "Invalid Workshop remainder count"
                        );
                    }
                    List<PreviewStack> remainders =
                            new ArrayList<>(remainderCount);
                    for (int index = 0; index < remainderCount; index++) {
                        remainders.add(
                                PreviewStack.STREAM_CODEC.decode(buffer)
                        );
                    }
                    long revision = ByteBufCodecs.VAR_LONG.decode(buffer);
                    String failure =
                            ByteBufCodecs.STRING_UTF8.decode(buffer);
                    return new WorkshopPreviewPayload(
                            containerId,
                            sequence,
                            recipeId,
                            requested,
                            executable,
                            consumed,
                            produced,
                            durability,
                            remainders,
                            revision,
                            failure
                    );
                }

                @Override
                public void encode(
                        ByteBuf buffer,
                        WorkshopPreviewPayload payload
                ) {
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.containerId()
                    );
                    ByteBufCodecs.VAR_LONG.encode(
                            buffer,
                            payload.sequence()
                    );
                    IDENTIFIER_CODEC.encode(buffer, payload.recipeId());
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.requested()
                    );
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.executable()
                    );
                    PreviewStack.STREAM_CODEC.encode(
                            buffer,
                            payload.consumed()
                    );
                    PreviewStack.STREAM_CODEC.encode(
                            buffer,
                            payload.produced()
                    );
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.durabilityCost()
                    );
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.remainders().size()
                    );
                    payload.remainders().forEach(remainder ->
                            PreviewStack.STREAM_CODEC.encode(
                                    buffer,
                                    remainder
                            )
                    );
                    ByteBufCodecs.VAR_LONG.encode(
                            buffer,
                            payload.revision()
                    );
                    ByteBufCodecs.STRING_UTF8.encode(
                            buffer,
                            payload.failure()
                    );
                }
            };

    public WorkshopPreviewPayload {
        remainders = List.copyOf(remainders);
    }

    public static WorkshopPreviewPayload from(
            int containerId,
            long sequence,
            Identifier recipeId,
            OperationPreview preview
    ) {
        return new WorkshopPreviewPayload(
                containerId,
                sequence,
                recipeId,
                preview.requested(),
                preview.executable(),
                PreviewStack.from(preview.consumed()),
                PreviewStack.from(preview.produced()),
                preview.durabilityCost(),
                preview.remainders().stream()
                        .map(PreviewStack::from)
                        .toList(),
                preview.revision(),
                preview.failure()
        );
    }

    public static WorkshopPreviewPayload rejected(
            int containerId,
            long sequence,
            Identifier recipeId,
            int requested,
            long revision,
            String failure
    ) {
        return new WorkshopPreviewPayload(
                containerId,
                sequence,
                recipeId,
                requested,
                0,
                PreviewStack.EMPTY,
                PreviewStack.EMPTY,
                0,
                List.of(),
                revision,
                failure
        );
    }

    public boolean exact() {
        return executable > 0 && executable == requested;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
