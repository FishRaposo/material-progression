package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.block.entity.BulkCraftingTableBlockEntity.BulkPreview;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BulkCraftingPreviewPayload(
        int containerId,
        long sequence,
        String target,
        int requested,
        int maxQuantity,
        List<String> targets,
        List<PreviewStack> costs,
        List<PreviewStack> missing,
        List<PreviewStack> surplus,
        long fingerprint,
        String failure
) implements CustomPacketPayload {
    private static final int MAX_ENTRIES = 256;
    public static final Type<BulkCraftingPreviewPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "bulk_crafting_preview"
            ));
    public static final StreamCodec<ByteBuf, BulkCraftingPreviewPayload>
            STREAM_CODEC = new StreamCodec<>() {
                @Override
                public BulkCraftingPreviewPayload decode(ByteBuf buffer) {
                    return new BulkCraftingPreviewPayload(
                            ByteBufCodecs.VAR_INT.decode(buffer),
                            ByteBufCodecs.VAR_LONG.decode(buffer),
                            ByteBufCodecs.STRING_UTF8.decode(buffer),
                            ByteBufCodecs.VAR_INT.decode(buffer),
                            ByteBufCodecs.VAR_INT.decode(buffer),
                            decodeStrings(buffer),
                            decodeStacks(buffer),
                            decodeStacks(buffer),
                            decodeStacks(buffer),
                            ByteBufCodecs.VAR_LONG.decode(buffer),
                            ByteBufCodecs.STRING_UTF8.decode(buffer)
                    );
                }

                @Override
                public void encode(
                        ByteBuf buffer,
                        BulkCraftingPreviewPayload payload
                ) {
                    ByteBufCodecs.VAR_INT.encode(
                            buffer,
                            payload.containerId
                    );
                    ByteBufCodecs.VAR_LONG.encode(buffer, payload.sequence);
                    ByteBufCodecs.STRING_UTF8.encode(buffer, payload.target);
                    ByteBufCodecs.VAR_INT.encode(buffer, payload.requested);
                    ByteBufCodecs.VAR_INT.encode(buffer, payload.maxQuantity);
                    encodeStrings(buffer, payload.targets);
                    encodeStacks(buffer, payload.costs);
                    encodeStacks(buffer, payload.missing);
                    encodeStacks(buffer, payload.surplus);
                    ByteBufCodecs.VAR_LONG.encode(
                            buffer,
                            payload.fingerprint
                    );
                    ByteBufCodecs.STRING_UTF8.encode(
                            buffer,
                            payload.failure
                    );
                }
            };

    public BulkCraftingPreviewPayload {
        targets = List.copyOf(targets);
        costs = List.copyOf(costs);
        missing = List.copyOf(missing);
        surplus = List.copyOf(surplus);
        if (targets.size() > MAX_ENTRIES
                || costs.size() > MAX_ENTRIES
                || missing.size() > MAX_ENTRIES
                || surplus.size() > MAX_ENTRIES) {
            throw new IllegalArgumentException(
                    "Bulk crafting preview exceeds its entry limit"
            );
        }
    }

    public static BulkCraftingPreviewPayload from(
            int containerId,
            long sequence,
            List<String> targets,
            BulkPreview preview
    ) {
        Map<String, Integer> missing = preview.plan()
                .failure()
                .map(failure -> failure.missing())
                .orElse(Map.of());
        return new BulkCraftingPreviewPayload(
                containerId,
                sequence,
                preview.target(),
                preview.requested(),
                preview.maxQuantity(),
                targets,
                stacks(preview.plan().consumed()),
                stacks(missing),
                stacks(preview.plan().surplus()),
                preview.fingerprint(),
                preview.failure()
        );
    }

    public static BulkCraftingPreviewPayload rejected(
            int containerId,
            long sequence,
            List<String> targets,
            String target,
            int requested,
            String failure
    ) {
        return new BulkCraftingPreviewPayload(
                containerId,
                sequence,
                target,
                requested,
                0,
                targets,
                List.of(),
                List.of(),
                List.of(),
                0,
                failure
        );
    }

    public boolean executable() {
        return failure.isEmpty() && requested > 0
                && requested <= maxQuantity;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static List<PreviewStack> stacks(Map<String, Integer> counts) {
        return counts.entrySet().stream()
                .map(entry -> new PreviewStack(
                        Identifier.parse(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }

    private static List<String> decodeStrings(ByteBuf buffer) {
        int size = size(buffer);
        List<String> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(ByteBufCodecs.STRING_UTF8.decode(buffer));
        }
        return List.copyOf(values);
    }

    private static void encodeStrings(
            ByteBuf buffer,
            List<String> values
    ) {
        ByteBufCodecs.VAR_INT.encode(buffer, values.size());
        values.forEach(value ->
                ByteBufCodecs.STRING_UTF8.encode(buffer, value));
    }

    private static List<PreviewStack> decodeStacks(ByteBuf buffer) {
        int size = size(buffer);
        List<PreviewStack> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(PreviewStack.STREAM_CODEC.decode(buffer));
        }
        return List.copyOf(values);
    }

    private static void encodeStacks(
            ByteBuf buffer,
            List<PreviewStack> values
    ) {
        ByteBufCodecs.VAR_INT.encode(buffer, values.size());
        values.forEach(value ->
                PreviewStack.STREAM_CODEC.encode(buffer, value));
    }

    private static int size(ByteBuf buffer) {
        int size = ByteBufCodecs.VAR_INT.decode(buffer);
        if (size < 0 || size > MAX_ENTRIES) {
            throw new IllegalArgumentException(
                    "Invalid Bulk crafting preview entry count"
            );
        }
        return size;
    }
}
