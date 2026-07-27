package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MaterialProgression.MOD_ID)
public final class MaterialProgression {
    public static final String MOD_ID = "material_progression";

    public MaterialProgression(IEventBus modBus) {
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModRecipes.register(modBus);
    }
}
