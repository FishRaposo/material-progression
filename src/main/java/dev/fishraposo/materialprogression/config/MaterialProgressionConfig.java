package dev.fishraposo.materialprogression.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MaterialProgressionConfig {
    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();
    static final ModConfigSpec.IntValue CONFIG_VERSION = BUILDER
            .comment("Material Progression server config schema version.")
            .defineInRange("configVersion", 1, 1, 1);
    static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS = BUILDER
            .comment("Require an item in #minecraft:axes for logs to drop.")
            .translation("config.material_progression.server.requireAxeForLogs")
            .define("requireAxeForLogs", true);
    static final ModConfigSpec.BooleanValue KNIFE_PLANT_HARVESTING = BUILDER
            .comment(
                    "Make items in #c:tools/knives harvest suitable plants as fiber."
            )
            .translation(
                    "config.material_progression.server.knifePlantHarvesting"
            )
            .define("knifePlantHarvesting", true);
    static final ModConfigSpec.BooleanValue STONE_ROCK_HARVESTING = BUILDER
            .comment(
                    "Make mined natural stone drop four Rocks instead of cobblestone."
            )
            .translation(
                    "config.material_progression.server.stoneRockHarvesting"
            )
            .define("stoneRockHarvesting", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private MaterialProgressionConfig() {
    }

    public static int configVersion() {
        return CONFIG_VERSION.get();
    }

    public static boolean requireAxeForLogs() {
        return REQUIRE_AXE_FOR_LOGS.get();
    }

    public static boolean knifePlantHarvesting() {
        return KNIFE_PLANT_HARVESTING.get();
    }

    public static boolean stoneRockHarvesting() {
        return STONE_ROCK_HARVESTING.get();
    }
}
