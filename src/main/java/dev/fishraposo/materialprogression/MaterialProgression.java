package dev.fishraposo.materialprogression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.progression.HarvestRuleEvents;
import dev.fishraposo.materialprogression.progression.PlantFiberHarvestEvents;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModFeatures;
import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.stone.GeologyDimensionProfileReloadListener;
import dev.fishraposo.materialprogression.stone.GeologyMiningNetwork;
import dev.fishraposo.materialprogression.stone.StoneFamilyReloadListener;
import dev.fishraposo.materialprogression.stone.GeologyFeedbackEvents;
import dev.fishraposo.materialprogression.stone.GeologyMiningEvents;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneEvents;
import dev.fishraposo.materialprogression.world.level.block.LooseRockInvalidationEvents;
import dev.fishraposo.materialprogression.world.level.block.ExternalLooseRockDrops;
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
        ModDataAttachments.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModRecipes.register(modBus);
        ModFeatures.register(modBus);
        GeologyMiningNetwork.register(modBus);
        container.registerConfig(
                ModConfig.Type.SERVER,
                MaterialProgressionConfig.SPEC
        );
        HarvestRuleEvents.register();
        PlantFiberHarvestEvents.register();
        GeologyFeedbackEvents.register();
        GeologyMiningEvents.register();
        ExternalLooseRockDrops.register();
        LooseRockInvalidationEvents.register();
        PlacedRawStoneEvents.register();
        GeologyDimensionProfileReloadListener.register();
        StoneFamilyReloadListener.register();
    }
}
