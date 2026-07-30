package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.levelgen.feature.LooseRocksFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(
                    BuiltInRegistries.FEATURE,
                    MaterialProgression.MOD_ID
            );

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            LOOSE_ROCKS = FEATURES.register(
                    "loose_rocks",
                    () -> new LooseRocksFeature(NoneFeatureConfiguration.CODEC)
            );

    private ModFeatures() {
    }

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
