package dev.fishraposo.materialprogression.stone;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Common payload registration and server-only request handling.
 */
public final class GeologyMiningNetwork {
    private static final String NETWORK_VERSION = "1";

    private GeologyMiningNetwork() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(GeologyMiningNetwork::registerPayloads);
    }

    private static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {
        event.registrar(NETWORK_VERSION)
                .playToServer(
                        GeologyMiningSnapshotRequest.TYPE,
                        GeologyMiningSnapshotRequest.STREAM_CODEC,
                        GeologyMiningNetwork::handleRequest
                )
                .playToClient(
                        GeologyMiningSnapshotPayload.TYPE,
                        GeologyMiningSnapshotPayload.STREAM_CODEC
                );
    }

    private static void handleRequest(
            GeologyMiningSnapshotRequest request,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        GeologyMiningSnapshotService.resolve(player, request)
                .ifPresent(context::reply);
    }
}
