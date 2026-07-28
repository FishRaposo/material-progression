package dev.fishraposo.materialprogression.config;

import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

public final class ConfigFixture {
    private ConfigFixture() {
    }

    public static void setRequireAxeForLogs(
            ExtendedGameTestHelper helper,
            boolean value
    ) {
        boolean previous = MaterialProgressionConfig.REQUIRE_AXE_FOR_LOGS.get();
        MaterialProgressionConfig.REQUIRE_AXE_FOR_LOGS.set(value);
        helper.addEndListener(
                ignored -> MaterialProgressionConfig.REQUIRE_AXE_FOR_LOGS.set(
                        previous
                )
        );
    }
}
