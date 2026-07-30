package dev.fishraposo.materialprogression.gametest;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.testframework.conf.FrameworkConfiguration;

@Mod(MaterialProgressionGameTestMod.MOD_ID)
public final class MaterialProgressionGameTestMod {
    public static final String MOD_ID = "material_progression_gametests";
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MOD_ID);
    static final DeferredItem<Item> UNKNOWN_ROCK =
            ITEMS.registerSimpleItem("unknown_rock");

    public MaterialProgressionGameTestMod(IEventBus modBus, ModContainer container) {
        ITEMS.register(modBus);
        FrameworkConfiguration.builder(
                        Identifier.fromNamespaceAndPath(MOD_ID, "tests")
                )
                .build()
                .create()
                .init(modBus, container);
    }
}
