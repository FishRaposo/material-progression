package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.client.ClientWorkshopPreviews;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                SelectWorkshopRecipePayload.TYPE,
                SelectWorkshopRecipePayload.STREAM_CODEC,
                SelectWorkshopRecipePayload::handle
        );
        registrar.playToServer(
                RequestWorkshopPreviewPayload.TYPE,
                RequestWorkshopPreviewPayload.STREAM_CODEC,
                RequestWorkshopPreviewPayload::handle
        );
        registrar.playToServer(
                ExecuteWorkshopBatchPayload.TYPE,
                ExecuteWorkshopBatchPayload.STREAM_CODEC,
                ExecuteWorkshopBatchPayload::handle
        );
        registrar.playToClient(
                WorkshopPreviewPayload.TYPE,
                WorkshopPreviewPayload.STREAM_CODEC,
                (payload, context) ->
                        ClientWorkshopPreviews.accept(payload)
        );
    }
}
