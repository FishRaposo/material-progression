package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.data.MaterialProgressionData;
import dev.fishraposo.materialprogression.progression.HarvestRuleEvents;
import dev.fishraposo.materialprogression.network.ModNetwork;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

@Mod(MaterialProgression.MOD_ID)
public final class MaterialProgression {
    public static final String MOD_ID = "material_progression";

    public MaterialProgression(IEventBus modBus, ModContainer container) {
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModRecipes.register(modBus);
        ModNetwork.register(modBus);
        modBus.addListener(MaterialProgressionData::gatherData);
        container.registerConfig(
                ModConfig.Type.SERVER,
                MaterialProgressionConfig.SPEC
        );
        HarvestRuleEvents.register();
    }
}
