package dev.fishraposo.materialprogression.gametest;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
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
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::protectExplosionFixture
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::configureFluidFixture
        );
        FrameworkConfiguration.builder(
                        Identifier.fromNamespaceAndPath(MOD_ID, "tests")
                )
                .build()
                .create()
                .init(modBus, container);
    }

    private static void configureFluidFixture(
            BlockEvent.FluidPlaceBlockEvent event
    ) {
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.LAVA)
                && event.getLevel()
                        .getBlockState(event.getPos().below())
                        .is(Blocks.GRANITE)
                && event.getLevel()
                        .getBlockState(event.getPos().below(2))
                        .is(Blocks.DIAMOND_BLOCK)) {
            event.setNewState(Blocks.STONE.defaultBlockState());
        }
    }

    private static void protectExplosionFixture(
            ExplosionEvent.Detonate event
    ) {
        event.getAffectedBlocks().stream()
                .filter(pos ->
                        event.getLevel().getBlockState(pos).is(Blocks.GRANITE)
                                && event.getLevel()
                                        .getBlockState(pos.below())
                                        .is(Blocks.DIAMOND_BLOCK)
                )
                .findFirst()
                .ifPresent(source ->
                        event.getAffectedBlocks().removeIf(
                                affected -> !affected.equals(source)
                        )
                );
    }
}
