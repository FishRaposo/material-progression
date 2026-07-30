package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.MaterialProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class OpeningAdvancements {
    private static final Identifier DENSE_GEOLOGY =
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "progression/dense_geology"
            );

    private OpeningAdvancements() {
    }

    public static void awardDenseGeology(ServerPlayer player) {
        var server = player.level().getServer();
        var advancement = server.getAdvancements().get(DENSE_GEOLOGY);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "dense_geology");
        }
    }
}
