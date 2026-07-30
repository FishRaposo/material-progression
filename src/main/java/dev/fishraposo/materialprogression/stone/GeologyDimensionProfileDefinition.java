package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;

/**
 * Datapack definition for geology depth bands in a non-built-in dimension.
 *
 * <p>Bands are evaluated in declaration order with inclusive
 * {@code y >= minimum_y} comparisons. Thresholds must be strictly descending,
 * and the final band must be a catch-all without {@code minimum_y}.
 */
public record GeologyDimensionProfileDefinition(
        Identifier dimension,
        List<Band> bands
) {
    private static final Codec<GeologyDimensionProfileDefinition> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC
                            .fieldOf("dimension")
                            .forGetter(
                                    GeologyDimensionProfileDefinition::dimension
                            ),
                    Band.CODEC
                            .listOf()
                            .fieldOf("bands")
                            .forGetter(
                                    GeologyDimensionProfileDefinition::bands
                            )
            ).apply(instance, GeologyDimensionProfileDefinition::new));

    public static final Codec<GeologyDimensionProfileDefinition> CODEC =
            RAW_CODEC.validate(
                    GeologyDimensionProfileDefinition::validateForCodec
            );

    public GeologyDimensionProfileDefinition {
        bands = List.copyOf(bands);
    }

    private static DataResult<GeologyDimensionProfileDefinition>
            validateForCodec(GeologyDimensionProfileDefinition definition) {
        Optional<String> error = definition.validationError();
        if (error.isPresent()) {
            return DataResult.error(error::orElseThrow);
        }
        return DataResult.success(definition);
    }

    void validateOrThrow(Identifier resourceId) {
        validationError().ifPresent(message -> {
            throw new IllegalStateException(
                    "Invalid geology dimension profile "
                            + resourceId
                            + ": "
                            + message
            );
        });
    }

    private Optional<String> validationError() {
        if (bands.isEmpty()) {
            return Optional.of("bands must not be empty");
        }

        Integer previousMinimum = null;
        for (int index = 0; index < bands.size(); index++) {
            Band band = bands.get(index);
            if (band.level() < 0 || band.level() > 3) {
                return Optional.of(
                        "band " + index + " level must be within 0 through 3"
                );
            }

            boolean last = index == bands.size() - 1;
            if (band.minimumY().isEmpty()) {
                if (!last) {
                    return Optional.of(
                            "catch-all band without minimum_y must be final"
                    );
                }
                continue;
            }
            if (last) {
                return Optional.of(
                        "bands must end with a catch-all band without minimum_y"
                );
            }

            int minimum = band.minimumY().orElseThrow();
            if (previousMinimum != null && minimum >= previousMinimum) {
                return Optional.of(
                        "minimum_y values must be strictly descending; found "
                                + minimum
                                + " after "
                                + previousMinimum
                );
            }
            previousMinimum = minimum;
        }
        return Optional.empty();
    }

    public record Band(Optional<Integer> minimumY, int level) {
        public static final Codec<Band> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.INT
                                .optionalFieldOf("minimum_y")
                                .forGetter(Band::minimumY),
                        Codec.intRange(0, 3)
                                .fieldOf("level")
                                .forGetter(Band::level)
                ).apply(instance, Band::new));
    }
}
