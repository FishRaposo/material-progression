package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestWorkshopPreviewPayload(
        int containerId,
        Identifier recipeId,
        int requested
) implements CustomPacketPayload {
    public static final Type<RequestWorkshopPreviewPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "request_workshop_preview"
            )
    );
    public static final StreamCodec<ByteBuf, RequestWorkshopPreviewPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RequestWorkshopPreviewPayload::containerId,
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    RequestWorkshopPreviewPayload::recipeId,
                    ByteBufCodecs.VAR_INT,
                    RequestWorkshopPreviewPayload::requested,
                    RequestWorkshopPreviewPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(
            RequestWorkshopPreviewPayload payload,
            IPayloadContext context
    ) {
        if (context.player().containerMenu instanceof WorkshopMenu menu
                && menu.containerId == payload.containerId()) {
            menu.sendPreview(
                    context.player(),
                    payload.recipeId(),
                    payload.requested()
            );
        }
    }
}
