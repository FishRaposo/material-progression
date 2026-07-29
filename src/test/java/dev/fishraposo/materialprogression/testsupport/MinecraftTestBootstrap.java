package dev.fishraposo.materialprogression.testsupport;

import net.minecraft.SharedConstants;
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
        bootstrapped = true;
    }
}
