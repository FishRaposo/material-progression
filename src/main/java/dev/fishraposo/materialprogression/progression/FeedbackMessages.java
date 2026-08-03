package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
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
            StoneFamilyCatalog.Entry family,
            GeologyTier tier
    ) {
        return Component.translatable(
                "message.material_progression.geology.insufficient",
                Component.translatable(
                        "message.material_progression.geology.tier."
                                + tier.level()
                ),
                family.displayName(),
                Component.translatable(
                        "message.material_progression.geology.capability."
                                + tier.level()
                )
        );
    }

    public static Component correctToolRequired(
            StoneFamilyCatalog.Entry family
    ) {
        return Component.translatable(
                "message.material_progression.geology.correct_tool",
                family.displayName()
        );
    }

    public static Component prospectingHint(String material, String direction) {
        return Component.translatable(
                "message.material_progression.prospecting.hint",
                Component.translatable("material.material_progression." + material),
                Component.translatable("message.material_progression.direction." + direction)
        );
    }

    public static Component oreToolRequired(String material, boolean gravel) {
        return Component.translatable(
                gravel
                        ? "message.material_progression.ore.shovel_required"
                        : "message.material_progression.ore.pickaxe_required",
                Component.translatable("material.material_progression." + material)
        );
    }
}
