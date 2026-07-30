package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneMarkers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    MaterialProgression.MOD_ID
            );

    public static final DeferredHolder<
            AttachmentType<?>,
            AttachmentType<PlacedRawStoneMarkers>
            > PLACED_RAW_STONES = ATTACHMENTS.register(
                    "placed_raw_stones",
                    () -> AttachmentType.builder(PlacedRawStoneMarkers::new)
                            .serialize(
                                    PlacedRawStoneMarkers.CODEC,
                                    markers -> !markers.isEmpty()
                            )
                            .build()
            );
    public static final DeferredHolder<
            AttachmentType<?>,
            AttachmentType<Long>
            > GEOLOGY_FEEDBACK_TICK = ATTACHMENTS.register(
                    "geology_feedback_tick",
                    () -> AttachmentType.builder(() -> -20L).build()
            );
    public static final DeferredHolder<
            AttachmentType<?>,
            AttachmentType<Long>
            > LOG_FEEDBACK_TICK = ATTACHMENTS.register(
                    "log_feedback_tick",
                    () -> AttachmentType.builder(() -> -20L).build()
            );

    private ModDataAttachments() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }
}
