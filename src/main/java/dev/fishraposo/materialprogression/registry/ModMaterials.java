package dev.fishraposo.materialprogression.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Public, immutable vocabulary for Material Progression consumers.
 *
 * <p>The registry-facing implementation deliberately keeps the material
 * identity separate from individual block forms: an ore is still Copper when
 * it is embedded in Tuff, Gravel, Netherrack, or End Stone. Dependent mods
 * can use this class for identifiers and forms without copying our private
 * registration maps.</p>
 */
public final class ModMaterials {
    public record MaterialProfile(
            String id,
            int harvestLevel,
            int durability,
            float speed,
            String behavior
    ) {}

    public record OreProfile(
            String id,
            int harvestLevel,
            List<String> hosts,
            boolean gravelEvidence
    ) {}

    public static final List<String> OVERWORLD_HOSTS = List.of(
            "stone", "granite", "diorite", "andesite", "deepslate",
            "tuff", "calcite", "dripstone", "sulfur", "cinnabar",
            "sandstone", "red_sandstone"
    );
    public static final List<String> NETHER_HOSTS = List.of(
            "netherrack", "basalt", "blackstone"
    );
    public static final List<String> END_HOSTS = List.of("end_stone");
    public static final List<String> EQUIPMENT_MATERIALS = List.of(
            "wood", "stone", "flint", "copper", "tin", "bronze",
            "steel", "zinc", "brass", "lead", "nickel", "invar",
            "silver", "rose_gold"
    );

    private static final Map<String, MaterialProfile> MATERIALS = profiles();
    private static final Map<String, OreProfile> ORES = oreProfiles();

    private ModMaterials() {}

    public static List<MaterialProfile> materials() {
        return List.copyOf(MATERIALS.values());
    }

    public static List<OreProfile> ores() {
        return List.copyOf(ORES.values());
    }

    public static Optional<MaterialProfile> material(String id) {
        return Optional.ofNullable(MATERIALS.get(id));
    }

    public static Optional<OreProfile> ore(String id) {
        return Optional.ofNullable(ORES.get(id));
    }

    public static List<String> oreBlockIds() {
        return ORES.values().stream()
                .flatMap(profile -> profile.hosts().stream()
                        .map(host -> oreBlockId(host, profile.id())))
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        blocks -> {
                            blocks.addAll(ORES.keySet().stream()
                                    .map(id -> "gravel_" + id + "_ore")
                                    .toList());
                            return List.copyOf(blocks);
                        }
                ));
    }

    public static Optional<String> oreMaterialForBlock(String blockId) {
        if (blockId.startsWith("gravel_") && blockId.endsWith("_ore")) {
            String id = blockId.substring("gravel_".length(), blockId.length() - "_ore".length());
            return ORES.containsKey(id) ? Optional.of(id) : Optional.empty();
        }
        return ORES.values().stream()
                .filter(profile -> profile.hosts().stream()
                        .anyMatch(host -> oreBlockId(host, profile.id()).equals(blockId)))
                .map(OreProfile::id)
                .findFirst();
    }

    private static Map<String, MaterialProfile> profiles() {
        Map<String, MaterialProfile> profiles = new LinkedHashMap<>();
        add(profiles, "wood", 0, 59, 2.0F, "primitive");
        add(profiles, "stone", 1, 131, 4.0F, "heavy");
        add(profiles, "flint", 1, 96, 5.0F, "primitive");
        add(profiles, "copper", 1, 180, 5.0F, "balanced");
        add(profiles, "tin", 1, 160, 4.5F, "balanced");
        add(profiles, "bronze", 2, 360, 6.5F, "heat_safe");
        add(profiles, "steel", 2, 720, 7.0F, "hardwearing");
        add(profiles, "zinc", 1, 210, 5.5F, "light");
        add(profiles, "brass", 2, 380, 6.5F, "responsive");
        add(profiles, "lead", 1, 440, 3.5F, "heavy");
        add(profiles, "nickel", 2, 820, 6.8F, "hardwearing");
        add(profiles, "invar", 2, 1200, 6.2F, "heat_safe_hardwearing");
        add(profiles, "silver", 1, 250, 6.0F, "precious");
        add(profiles, "rose_gold", 2, 340, 7.0F, "responsive");
        return Map.copyOf(profiles);
    }

    private static Map<String, OreProfile> oreProfiles() {
        Map<String, OreProfile> ores = new LinkedHashMap<>();
        add(ores, "copper", 1, OVERWORLD_HOSTS);
        add(ores, "tin", 1, OVERWORLD_HOSTS);
        add(ores, "zinc", 1, OVERWORLD_HOSTS);
        add(ores, "lead", 1, OVERWORLD_HOSTS);
        add(ores, "nickel", 2, concat(OVERWORLD_HOSTS, NETHER_HOSTS));
        add(ores, "silver", 2, concat(OVERWORLD_HOSTS, END_HOSTS));
        return Map.copyOf(ores);
    }

    private static void add(
            Map<String, MaterialProfile> profiles, String id, int level,
            int durability, float speed, String behavior
    ) {
        profiles.put(id, new MaterialProfile(id, level, durability, speed, behavior));
    }

    private static void add(Map<String, OreProfile> ores, String id, int level, List<String> hosts) {
        ores.put(id, new OreProfile(id, level, hosts, true));
    }

    private static List<String> concat(List<String> left, List<String> right) {
        return java.util.stream.Stream.concat(left.stream(), right.stream()).toList();
    }

    public static String oreBlockId(String host, String material) {
        return host.equals("stone") ? material + "_ore" : host + "_" + material + "_ore";
    }
}
