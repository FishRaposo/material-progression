package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestBulkCraftingPreviewPayload(
        int containerId,
        String query,
        String target,
        int requested
) implements CustomPacketPayload {
    public static final Type<RequestBulkCraftingPreviewPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "request_bulk_crafting_preview"
            ));
    public static final StreamCodec<ByteBuf, RequestBulkCraftingPreviewPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RequestBulkCraftingPreviewPayload::containerId,
                    ByteBufCodecs.STRING_UTF8,
                    RequestBulkCraftingPreviewPayload::query,
                    ByteBufCodecs.STRING_UTF8,
                    RequestBulkCraftingPreviewPayload::target,
                    ByteBufCodecs.VAR_INT,
                    RequestBulkCraftingPreviewPayload::requested,
                    RequestBulkCraftingPreviewPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(
            RequestBulkCraftingPreviewPayload payload,
            IPayloadContext context
    ) {
        if (context.player().containerMenu
                instanceof BulkCraftingTableMenu menu
                && menu.containerId == payload.containerId()) {
            menu.sendPreview(
                    context.player(),
                    payload.query(),
                    payload.target(),
                    payload.requested()
            );
        }
    }
}
