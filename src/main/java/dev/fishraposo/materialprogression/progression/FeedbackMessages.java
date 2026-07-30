package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import net.minecraft.network.chat.Component;

public final class FeedbackMessages {
    private FeedbackMessages() {
    }

    public static Component logRequiresTool() {
        return Component.translatable(
                "message.material_progression.log.requires_tool"
        );
    }

    public static Component insufficientGeology(
            StoneFamily family,
            GeologyTier tier
    ) {
        return Component.translatable(
                "message.material_progression.geology.insufficient",
                Component.translatable(
                        "message.material_progression.geology.tier."
                                + tier.level()
                ),
                Component.translatable(
                        "stone_family.material_progression."
                                + family.getSerializedName()
                ),
                Component.translatable(
                        "message.material_progression.geology.capability."
                                + tier.level()
                )
        );
    }
}
