package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ExecuteWorkshopBatchPayload(
        int containerId,
        Identifier recipeId,
        int requested,
        long revision,
        long sequence
) implements CustomPacketPayload {
    public static final Type<ExecuteWorkshopBatchPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "execute_workshop_batch"
            )
    );
    public static final StreamCodec<ByteBuf, ExecuteWorkshopBatchPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ExecuteWorkshopBatchPayload::containerId,
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    ExecuteWorkshopBatchPayload::recipeId,
                    ByteBufCodecs.VAR_INT,
                    ExecuteWorkshopBatchPayload::requested,
                    ByteBufCodecs.VAR_LONG,
                    ExecuteWorkshopBatchPayload::revision,
                    ByteBufCodecs.VAR_LONG,
                    ExecuteWorkshopBatchPayload::sequence,
                    ExecuteWorkshopBatchPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(
            ExecuteWorkshopBatchPayload payload,
            IPayloadContext context
    ) {
        if (context.player().containerMenu instanceof WorkshopMenu menu
                && menu.containerId == payload.containerId()) {
            menu.executeBatch(
                    context.player(),
                    payload.recipeId(),
                    payload.requested(),
                    payload.revision(),
                    payload.sequence()
            );
        }
    }
}
