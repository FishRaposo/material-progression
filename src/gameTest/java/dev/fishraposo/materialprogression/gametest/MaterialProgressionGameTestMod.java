package dev.fishraposo.materialprogression.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
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
    static final DeferredBlock<Block> EXTERNAL_RAW_STONE =
            BLOCKS.registerSimpleBlock(
                    "external_raw_stone",
                    properties -> properties
                            .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F)
            );
    static final DeferredBlock<Block> EXTERNAL_DIRECT_SURFACE =
            BLOCKS.registerSimpleBlock(
                    "external_direct_surface",
                    properties -> properties
                            .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                            .strength(1.5F)
            );
    static final DeferredBlock<Block> EXTERNAL_COBBLE =
            BLOCKS.registerSimpleBlock(
                    "external_cobble",
                    properties -> properties
                            .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F)
            );
    static final DeferredItem<Item> UNKNOWN_ROCK =
            ITEMS.registerSimpleItem("unknown_rock");
    static final DeferredItem<Item> EXTERNAL_ROCK =
            ITEMS.registerSimpleItem("external_rock");
    static final DeferredItem<Item> EXTERNAL_ROCK_ALTERNATE =
            ITEMS.registerSimpleItem("external_rock_alternate");
    static final DeferredItem<?> VETO_RAW_STONE_ITEM =
            ITEMS.registerSimpleBlockItem("veto_raw_stone", VETO_RAW_STONE);
    static final DeferredItem<?> EXTERNAL_RAW_STONE_ITEM =
            ITEMS.registerSimpleBlockItem("external_raw_stone", EXTERNAL_RAW_STONE);
    static final DeferredItem<?> EXTERNAL_DIRECT_SURFACE_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "external_direct_surface",
                    EXTERNAL_DIRECT_SURFACE
            );
    static final DeferredItem<?> EXTERNAL_COBBLE_ITEM =
            ITEMS.registerSimpleBlockItem("external_cobble", EXTERNAL_COBBLE);
    static final DeferredItem<Item> SHARED_KNIFE =
            ITEMS.registerItem(
                    "shared_knife",
                    properties -> new Item(properties.durability(4))
            );
    private static BlockPos canceledPlacement;
    private static BlockPos canceledLivingDestruction;
    private static BlockPos canceledNeighborNotify;
    private static BlockPos canceledBreak;
    private static BlockPos nestedCanceledBreakMutation;
    private static BlockPos sentinelDrop;

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
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::addTargetedSentinelDrop
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                MaterialProgressionGameTestMod::cancelTargetedNeighborNotify
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::cancelTargetedPlacement
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::cancelTargetedLivingDestruction
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::cancelTargetedBreak
        );
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                MaterialProgressionGameTestMod::runNestedCanceledBreakMutation
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

    static void cancelNextNeighborNotifyAt(BlockPos pos) {
        canceledNeighborNotify = pos.immutable();
    }

    static void runNestedCanceledBreakMutationAt(BlockPos pos) {
        nestedCanceledBreakMutation = pos.immutable();
    }

    static void cancelNextBreakAt(BlockPos pos) {
        canceledBreak = pos.immutable();
    }

    static void addSentinelDropAt(BlockPos pos) {
        sentinelDrop = pos.immutable();
    }

    static void clearCancellations() {
        canceledPlacement = null;
        canceledLivingDestruction = null;
        canceledNeighborNotify = null;
        canceledBreak = null;
        nestedCanceledBreakMutation = null;
        sentinelDrop = null;
    }

    private static void addTargetedSentinelDrop(BlockDropsEvent event) {
        if (!event.getPos().equals(sentinelDrop)) {
            return;
        }
        sentinelDrop = null;
        event.getDrops().add(new ItemEntity(
                event.getLevel(),
                event.getPos().getX() + 0.5,
                event.getPos().getY() + 0.5,
                event.getPos().getZ() + 0.5,
                new ItemStack(Items.STICK)
        ));
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

    private static void cancelTargetedNeighborNotify(
            BlockEvent.NeighborNotifyEvent event
    ) {
        if (event.getPos().equals(canceledNeighborNotify)) {
            canceledNeighborNotify = null;
            event.setCanceled(true);
        }
    }

    private static void cancelTargetedBreak(BreakBlockEvent event) {
        if (event.getPos().equals(canceledBreak)) {
            canceledBreak = null;
            event.setCanceled(true);
        }
    }

    private static void runNestedCanceledBreakMutation(
            LivingDestroyBlockEvent event
    ) {
        if (!event.getPos().equals(nestedCanceledBreakMutation)) {
            return;
        }
        nestedCanceledBreakMutation = null;
        canceledBreak = event.getPos().immutable();
        var nested = NeoForge.EVENT_BUS.post(new BreakBlockEvent(
                event.getEntity().level(),
                event.getPos(),
                event.getState(),
                (Player) event.getEntity()
        ));
        if (!nested.isCanceled()) {
            throw new IllegalStateException(
                    "Nested BreakBlockEvent fixture was not canceled"
            );
        }
        event.getEntity().level().setBlock(
                event.getPos(),
                Blocks.AIR.defaultBlockState(),
                Block.UPDATE_ALL
        );
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
        static final IntegerProperty BREAK_MODE =
                IntegerProperty.create("break_mode", 0, 2);

        private VetoRawStoneBlock(BlockBehaviour.Properties properties) {
            super(properties);
            registerDefaultState(
                    stateDefinition.any().setValue(BREAK_MODE, 0)
            );
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
            int breakMode = state.getValue(BREAK_MODE);
            if (breakMode == 1) {
                level.setBlock(
                        pos,
                        state.setValue(BREAK_MODE, 0),
                        Block.UPDATE_CLIENTS
                );
            } else if (breakMode == 2) {
                level.setBlock(
                        pos,
                        Blocks.AIR.defaultBlockState(),
                        Block.UPDATE_CLIENTS
                );
            }
            return false;
        }

        @Override
        protected void createBlockStateDefinition(
                StateDefinition.Builder<Block, BlockState> builder
        ) {
            builder.add(BREAK_MODE);
        }
    }
}
