package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ExecuteBulkCraftingPayload(
        int containerId,
        String target,
        int requested,
        long fingerprint,
        long sequence
) implements CustomPacketPayload {
    public static final Type<ExecuteBulkCraftingPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "execute_bulk_crafting"
            ));
    public static final StreamCodec<ByteBuf, ExecuteBulkCraftingPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ExecuteBulkCraftingPayload::containerId,
                    ByteBufCodecs.STRING_UTF8,
                    ExecuteBulkCraftingPayload::target,
                    ByteBufCodecs.VAR_INT,
                    ExecuteBulkCraftingPayload::requested,
                    ByteBufCodecs.VAR_LONG,
                    ExecuteBulkCraftingPayload::fingerprint,
                    ByteBufCodecs.VAR_LONG,
                    ExecuteBulkCraftingPayload::sequence,
                    ExecuteBulkCraftingPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(
            ExecuteBulkCraftingPayload payload,
            IPayloadContext context
    ) {
        if (context.player().containerMenu
                instanceof BulkCraftingTableMenu menu
                && menu.containerId == payload.containerId()) {
            menu.execute(
                    context.player(),
                    payload.target(),
                    payload.requested(),
                    payload.fingerprint(),
                    payload.sequence()
            );
        }
    }
}
