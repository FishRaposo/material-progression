package dev.fishraposo.materialprogression.gametest;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.testframework.conf.FrameworkConfiguration;

@Mod(MaterialProgressionGameTestMod.MOD_ID)
public final class MaterialProgressionGameTestMod {
    public static final String MOD_ID = "material_progression_gametests";

    public MaterialProgressionGameTestMod(IEventBus modBus, ModContainer container) {
        FrameworkConfiguration.builder(
                        Identifier.fromNamespaceAndPath(MOD_ID, "tests")
                )
                .build()
                .create()
                .init(modBus, container);
    }
}
