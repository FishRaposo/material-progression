package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import java.lang.reflect.Field;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class ConfigFixture {
    private static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS =
            findRequireAxeForLogs();

    private ConfigFixture() {
    }

    static void setRequireAxeForLogs(
            ExtendedGameTestHelper helper,
            boolean value
    ) {
        boolean previous = REQUIRE_AXE_FOR_LOGS.get();
        REQUIRE_AXE_FOR_LOGS.set(value);
        helper.addEndListener(
                ignored -> REQUIRE_AXE_FOR_LOGS.set(previous)
        );
    }

    private static ModConfigSpec.BooleanValue findRequireAxeForLogs() {
        try {
            Field field = MaterialProgressionConfig.class.getDeclaredField(
                    "REQUIRE_AXE_FOR_LOGS"
            );
            field.setAccessible(true);
            return (ModConfigSpec.BooleanValue) field.get(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Unable to access the log-harvest test config",
                    exception
            );
        }
    }
}
