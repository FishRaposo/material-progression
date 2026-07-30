package dev.fishraposo.materialprogression.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MaterialProgressionConfig {
    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();
    static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS = BUILDER
            .comment("Require an item in #minecraft:axes for logs to drop.")
            .translation("config.material_progression.server.requireAxeForLogs")
            .define("requireAxeForLogs", true);
    static final ModConfigSpec.BooleanValue ENABLE_GEOLOGICAL_HARDNESS = BUILDER
            .comment("Apply depth, family, exposure, and tool-capability geology rules.")
            .translation("config.material_progression.server.enableGeologicalHardness")
            .define("enableGeologicalHardness", true);
    static final ModConfigSpec.BooleanValue ENABLE_STONE_ROCK_DROPS = BUILDER
            .comment("Replace raw stone-family loot with matching Rocks.")
            .translation("config.material_progression.server.enableStoneRockDrops")
            .define("enableStoneRockDrops", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private MaterialProgressionConfig() {
    }

    public static boolean requireAxeForLogs() {
        return REQUIRE_AXE_FOR_LOGS.get();
    }

    public static boolean enableGeologicalHardness() {
        return ENABLE_GEOLOGICAL_HARDNESS.get();
    }

    public static boolean enableStoneRockDrops() {
        return ENABLE_STONE_ROCK_DROPS.get();
    }
}
