package dev.fishraposo.materialprogression.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.testframework.conf.FrameworkConfiguration;

@Mod(MaterialProgressionGameTestMod.MOD_ID)
public final class MaterialProgressionGameTestMod {
    public static final String MOD_ID = "material_progression_gametests";
    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MOD_ID);
    static final DeferredBlock<VetoRawStoneBlock> VETO_RAW_STONE =
            BLOCKS.registerBlock(
                    "veto_raw_stone",
                    VetoRawStoneBlock::new,
                    properties -> properties
                            .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                            .strength(1.5F)
            );
    static final DeferredItem<Item> UNKNOWN_ROCK =
            ITEMS.registerSimpleItem("unknown_rock");
    private static BlockPos canceledPlacement;
    private static BlockPos canceledLivingDestruction;

    public MaterialProgressionGameTestMod(IEventBus modBus, ModContainer container) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::protectExplosionFixture
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::configureFluidFixture
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::cancelTargetedPlacement
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::cancelTargetedLivingDestruction
        );
        FrameworkConfiguration.builder(
                        Identifier.fromNamespaceAndPath(MOD_ID, "tests")
                )
                .build()
                .create()
                .init(modBus, container);
    }

    static void cancelNextPlacementAt(BlockPos pos) {
        canceledPlacement = pos.immutable();
    }

    static void cancelNextLivingDestructionAt(BlockPos pos) {
        canceledLivingDestruction = pos.immutable();
    }

    static void clearCancellations() {
        canceledPlacement = null;
        canceledLivingDestruction = null;
    }

    private static void cancelTargetedPlacement(
            BlockEvent.EntityPlaceEvent event
    ) {
        if (event.getPos().equals(canceledPlacement)) {
            canceledPlacement = null;
            event.setCanceled(true);
        }
    }

    private static void cancelTargetedLivingDestruction(
            LivingDestroyBlockEvent event
    ) {
        if (event.getPos().equals(canceledLivingDestruction)) {
            canceledLivingDestruction = null;
            event.setCanceled(true);
        }
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

    static final class VetoRawStoneBlock extends Block {
        private VetoRawStoneBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public boolean onDestroyedByPlayer(
                BlockState state,
                Level level,
                BlockPos pos,
                Player player,
                ItemStack tool,
                boolean willHarvest,
                FluidState fluid
        ) {
            return false;
        }
    }
}
