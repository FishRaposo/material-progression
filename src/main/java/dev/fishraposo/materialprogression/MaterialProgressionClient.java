package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.client.CrusherScreen;
import dev.fishraposo.materialprogression.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = MaterialProgression.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaterialProgression.MOD_ID, value = Dist.CLIENT)
public final class MaterialProgressionClient {
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.CRUSHER.get(), CrusherScreen::new));
    }
}
