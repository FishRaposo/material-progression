package dev.fishraposo.materialprogression.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                SelectWorkshopRecipePayload.TYPE,
                SelectWorkshopRecipePayload.STREAM_CODEC,
                SelectWorkshopRecipePayload::handle
        );
    }
}
