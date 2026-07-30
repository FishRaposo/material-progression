package dev.fishraposo.materialprogression.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GroundStickConfiguration(
        BlockStateProvider toPlace,
        TagKey<Block> anchorTag,
        int attempts,
        int horizontalSpread,
        int surfaceVerticalRange,
        int anchorHorizontalRadius,
        int anchorVerticalRadius,
        float nearChance,
        float backgroundChance
) implements FeatureConfiguration {
    public static final Codec<GroundStickConfiguration> CODEC =
            RecordCodecBuilder.<GroundStickConfiguration>create(instance ->
                    instance.group(
                            BlockStateProvider.CODEC
                                    .fieldOf("to_place")
                                    .forGetter(GroundStickConfiguration::toPlace),
                            TagKey.hashedCodec(Registries.BLOCK)
                                    .fieldOf("anchor_tag")
                                    .forGetter(GroundStickConfiguration::anchorTag),
                            Codec.intRange(1, 32)
                                    .fieldOf("attempts")
                                    .forGetter(GroundStickConfiguration::attempts),
                            Codec.intRange(0, 16)
                                    .fieldOf("horizontal_spread")
                                    .forGetter(
                                            GroundStickConfiguration::horizontalSpread
                                    ),
                            Codec.intRange(0, 8)
                                    .fieldOf("surface_vertical_range")
                                    .forGetter(
                                            GroundStickConfiguration::
                                                    surfaceVerticalRange
                                    ),
                            Codec.intRange(1, 8)
                                    .fieldOf("anchor_horizontal_radius")
                                    .forGetter(
                                            GroundStickConfiguration::
                                                    anchorHorizontalRadius
                                    ),
                            Codec.intRange(0, 4)
                                    .fieldOf("anchor_vertical_radius")
                                    .forGetter(
                                            GroundStickConfiguration::
                                                    anchorVerticalRadius
                                    ),
                            ExtraCodecs.floatRange(0.0F, 1.0F)
                                    .fieldOf("near_chance")
                                    .forGetter(GroundStickConfiguration::nearChance),
                            ExtraCodecs.floatRange(0.0F, 1.0F)
                                    .fieldOf("background_chance")
                                    .forGetter(
                                            GroundStickConfiguration::
                                                    backgroundChance
                                    )
                    ).apply(instance, GroundStickConfiguration::new)
            ).validate(GroundStickConfiguration::validate);

    private static DataResult<GroundStickConfiguration> validate(
            GroundStickConfiguration configuration
    ) {
        if (configuration.nearChance() < configuration.backgroundChance()) {
            return DataResult.error(
                    () -> "near_chance must be greater than or equal to "
                            + "background_chance"
            );
        }
        return DataResult.success(configuration);
    }
}
