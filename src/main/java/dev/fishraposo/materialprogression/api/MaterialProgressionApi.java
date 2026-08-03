package dev.fishraposo.materialprogression.api;

import dev.fishraposo.materialprogression.registry.ModMaterials;
import java.util.List;
import java.util.Optional;

/**
 * Stable read-only material vocabulary for mods that build on Material
 * Progression.  Consumers should use these profiles and shared {@code c:}
 * tags rather than copying concrete item or block identifiers.
 */
public final class MaterialProgressionApi {
    private MaterialProgressionApi() {}

    public static List<ModMaterials.MaterialProfile> materials() {
        return ModMaterials.materials();
    }

    public static List<ModMaterials.OreProfile> ores() {
        return ModMaterials.ores();
    }

    public static Optional<ModMaterials.MaterialProfile> material(String id) {
        return ModMaterials.material(id);
    }

    public static Optional<ModMaterials.OreProfile> ore(String id) {
        return ModMaterials.ore(id);
    }
}
