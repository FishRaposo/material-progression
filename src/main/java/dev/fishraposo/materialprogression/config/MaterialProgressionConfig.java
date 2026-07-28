package dev.fishraposo.materialprogression.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MaterialProgressionConfig {
    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();
    static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS = BUILDER
            .comment("Require an item in #minecraft:axes for logs to drop.")
            .translation("config.material_progression.server.requireAxeForLogs")
            .define("requireAxeForLogs", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private MaterialProgressionConfig() {
    }

    public static boolean requireAxeForLogs() {
        return REQUIRE_AXE_FOR_LOGS.get();
    }
}
