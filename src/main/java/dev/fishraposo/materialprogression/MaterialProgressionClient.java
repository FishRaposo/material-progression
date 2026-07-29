package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.client.CrusherScreen;
import dev.fishraposo.materialprogression.client.ClientManualRecipes;
import dev.fishraposo.materialprogression.client.ClientWorkshopPreviews;
import dev.fishraposo.materialprogression.client.WorkshopScreen;
import dev.fishraposo.materialprogression.network.WorkshopPreviewPayload;
import dev.fishraposo.materialprogression.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@Mod(value = MaterialProgression.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaterialProgression.MOD_ID, value = Dist.CLIENT)
public final class MaterialProgressionClient {
    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CRUSHER.get(), CrusherScreen::new);
        event.register(ModMenus.WORKSHOP.get(), WorkshopScreen::new);
    }

    @SubscribeEvent
    static void receiveRecipes(RecipesReceivedEvent event) {
        ClientManualRecipes.replace(event.getRecipeMap());
    }

    @SubscribeEvent
    static void clearRecipes(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientManualRecipes.clear();
        ClientWorkshopPreviews.clear();
    }

    @SubscribeEvent
    static void registerClientPayloads(
            RegisterClientPayloadHandlersEvent event
    ) {
        event.register(
                WorkshopPreviewPayload.TYPE,
                (payload, context) ->
                        ClientWorkshopPreviews.accept(payload)
        );
    }
}
