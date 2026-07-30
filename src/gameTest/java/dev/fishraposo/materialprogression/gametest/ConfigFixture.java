package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import java.lang.reflect.Field;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class ConfigFixture {
    private static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS =
            findBoolean("REQUIRE_AXE_FOR_LOGS");
    private static final ModConfigSpec.BooleanValue ENABLE_GEOLOGICAL_HARDNESS =
            findBoolean("ENABLE_GEOLOGICAL_HARDNESS");
    private static final ModConfigSpec.BooleanValue ENABLE_STONE_ROCK_DROPS =
            findBoolean("ENABLE_STONE_ROCK_DROPS");

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

    static void setEnableGeologicalHardness(
            ExtendedGameTestHelper helper,
            boolean value
    ) {
        setBoolean(helper, ENABLE_GEOLOGICAL_HARDNESS, value);
    }

    static void setEnableStoneRockDrops(
            ExtendedGameTestHelper helper,
            boolean value
    ) {
        setBoolean(helper, ENABLE_STONE_ROCK_DROPS, value);
    }

    private static void setBoolean(
            ExtendedGameTestHelper helper,
            ModConfigSpec.BooleanValue config,
            boolean value
    ) {
        boolean previous = config.get();
        config.set(value);
        helper.addEndListener(ignored -> config.set(previous));
    }

    private static ModConfigSpec.BooleanValue findBoolean(String fieldName) {
        try {
            Field field = MaterialProgressionConfig.class.getDeclaredField(
                    fieldName
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
