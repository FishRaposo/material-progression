package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.client.CrusherScreen;
import dev.fishraposo.materialprogression.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = MaterialProgression.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaterialProgression.MOD_ID, value = Dist.CLIENT)
public final class MaterialProgressionClient {
    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CRUSHER.get(), CrusherScreen::new);
    }
}
