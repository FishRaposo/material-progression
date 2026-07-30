package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.client.CrusherScreen;
import dev.fishraposo.materialprogression.client.ExternalLooseRockRenderer;
import dev.fishraposo.materialprogression.client.ManualWorkshopRenderer;
import dev.fishraposo.materialprogression.client.ManualWorkshopScreen;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = MaterialProgression.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaterialProgression.MOD_ID, value = Dist.CLIENT)
public final class MaterialProgressionClient {
    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CRUSHER.get(), CrusherScreen::new);
        event.register(
                ModMenus.MANUAL_WORKSHOP.get(),
                ManualWorkshopScreen::new
        );
    }

    @SubscribeEvent
    static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.MANUAL_WORKSHOP.get(),
                ManualWorkshopRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.EXTERNAL_LOOSE_ROCKS.get(),
                ExternalLooseRockRenderer::new
        );
    }
}
