package dev.fishraposo.materialprogression.stone;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Immutable runtime snapshot of custom-dimension geology depth profiles.
 *
 * <p>The Overworld, Nether, and End are intentionally owned by the built-in
 * resolver rules and cannot be overridden through this catalog.
 */
public final class GeologyDimensionProfileCatalog {
    private static final Set<Identifier> BUILT_IN_DIMENSIONS = Set.of(
            Level.OVERWORLD.identifier(),
            Level.NETHER.identifier(),
            Level.END.identifier()
    );
    private static final GeologyDimensionProfileCatalog EMPTY =
            new GeologyDimensionProfileCatalog(Map.of());
    private static volatile GeologyDimensionProfileCatalog current = EMPTY;
    private static volatile long version;

    private final Map<Identifier, Profile> byDimension;

    private GeologyDimensionProfileCatalog(
            Map<Identifier, Profile> byDimension
    ) {
        this.byDimension = Map.copyOf(byDimension);
    }

    static GeologyDimensionProfileCatalog build(
            Map<Identifier, GeologyDimensionProfileDefinition> definitions
    ) {
        Map<Identifier, Profile> profiles = new HashMap<>();
        Map<Identifier, Identifier> owners = new HashMap<>();

        definitions.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> {
                    Identifier resourceId = entry.getKey();
                    GeologyDimensionProfileDefinition definition =
                            entry.getValue();
                    if (definition == null) {
                        throw invalid(resourceId, "definition is missing");
                    }
                    definition.validateOrThrow(resourceId);

                    Identifier dimension = definition.dimension();
                    if (BUILT_IN_DIMENSIONS.contains(dimension)) {
                        throw invalid(
                                resourceId,
                                "cannot override built-in dimension "
                                        + dimension
                        );
                    }

                    Identifier previous = owners.putIfAbsent(
                            dimension,
                            resourceId
                    );
                    if (previous != null) {
                        throw invalid(
                                resourceId,
                                "duplicate dimension "
                                        + dimension
                                        + " is already owned by "
                                        + previous
                        );
                    }
                    profiles.put(
                            dimension,
                            new Profile(definition.bands())
                    );
                });
        return new GeologyDimensionProfileCatalog(Map.copyOf(profiles));
    }

    static synchronized void install(
            GeologyDimensionProfileCatalog validatedCatalog
    ) {
        current = validatedCatalog;
        version++;
    }

    static synchronized void clear() {
        current = EMPTY;
        version++;
    }

    public static long version() {
        return version;
    }

    static int baseLevel(ResourceKey<Level> dimension, int y) {
        Profile profile = current.byDimension.get(dimension.identifier());
        return profile == null ? 0 : profile.baseLevel(y);
    }

    private static IllegalStateException invalid(
            Identifier resourceId,
            String message
    ) {
        return new IllegalStateException(
                "Invalid geology dimension profile "
                        + resourceId
                        + ": "
                        + message
        );
    }

    private record Profile(
            List<GeologyDimensionProfileDefinition.Band> bands
    ) {
        private Profile {
            bands = List.copyOf(bands);
        }

        int baseLevel(int y) {
            for (GeologyDimensionProfileDefinition.Band band : bands) {
                if (band.minimumY().isEmpty()
                        || y >= band.minimumY().orElseThrow()) {
                    return band.level();
                }
            }
            throw new IllegalStateException(
                    "Validated geology profile has no catch-all band"
            );
        }
    }
}
