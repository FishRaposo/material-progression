package dev.fishraposo.materialprogression.testsupport;

import java.lang.reflect.Field;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;

public final class MinecraftTestBootstrap {
    private static boolean bootstrapped;

    private MinecraftTestBootstrap() {
    }

    public static synchronized void bootStrap() {
        if (bootstrapped) {
            return;
        }

        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        bindDefaultComponents();
        bootstrapped = true;
    }

    private static void bindDefaultComponents() {
        try {
            Field field = BuiltInRegistries.class.getDeclaredField(
                    "DATA_COMPONENT_INITIALIZERS"
            );
            field.setAccessible(true);
            DataComponentInitializers initializers =
                    (DataComponentInitializers) field.get(null);
            initializers.build(VanillaRegistries.createLookup())
                    .forEach(DataComponentInitializers.PendingComponents::apply);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Could not bind Minecraft default data components for JVM tests",
                    exception
            );
        }
    }
}
