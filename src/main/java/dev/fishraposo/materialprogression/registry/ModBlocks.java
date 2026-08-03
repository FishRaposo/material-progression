package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.block.CrusherBlock;
import dev.fishraposo.materialprogression.world.level.block.ExternalLooseRocksBlock;
import dev.fishraposo.materialprogression.world.level.block.GroundResourceBlock;
import dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock;
import dev.fishraposo.materialprogression.world.level.block.ManualWorkshopBlock;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MaterialProgression.MOD_ID);

    public static final DeferredBlock<CrusherBlock> CRUSHER = BLOCKS.registerBlock(
            "crusher",
            CrusherBlock::new,
            properties -> properties
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .lightLevel(state -> state.getValue(CrusherBlock.LIT) ? 13 : 0)
    );

    private static final Map<String, DeferredBlock<Block>> ORE_BLOCKS =
            new LinkedHashMap<>();
    public static final DeferredBlock<Block> TIN_ORE;
    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE;

    static {
        for (String id : ModMaterials.oreBlockIds()) {
            ORE_BLOCKS.put(id, oreBlock(id));
        }
        TIN_ORE = ore("tin_ore");
        DEEPSLATE_TIN_ORE = ore("deepslate_tin_ore");
    }

    public static final DeferredBlock<ManualWorkshopBlock> MANUAL_WORKSHOP =
            BLOCKS.registerBlock(
                    "manual_workshop",
                    ManualWorkshopBlock::new,
                    properties -> properties
                            .mapColor(MapColor.WOOD)
                            .strength(2.5F)
                            .sound(SoundType.WOOD)
            );

    public static final DeferredBlock<Block> COBBLED_GRANITE = cobbled("cobbled_granite", MapColor.STONE, 2.0F);
    public static final DeferredBlock<Block> COBBLED_DIORITE = cobbled("cobbled_diorite", MapColor.STONE, 2.0F);
    public static final DeferredBlock<Block> COBBLED_ANDESITE = cobbled("cobbled_andesite", MapColor.STONE, 2.0F);
    public static final DeferredBlock<Block> COBBLED_TUFF = cobbled("cobbled_tuff", MapColor.STONE, 2.0F);
    public static final DeferredBlock<Block> COBBLED_CALCITE = cobbled("cobbled_calcite", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_DRIPSTONE = cobbled("cobbled_dripstone", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_SULFUR = cobbled("cobbled_sulfur", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_CINNABAR = cobbled("cobbled_cinnabar", MapColor.STONE, 2.0F);
    public static final DeferredBlock<Block> COBBLED_SANDSTONE = cobbled("cobbled_sandstone", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_RED_SANDSTONE = cobbled("cobbled_red_sandstone", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_NETHERRACK = cobbled("cobbled_netherrack", MapColor.STONE, 1.5F);
    public static final DeferredBlock<Block> COBBLED_BASALT = cobbled("cobbled_basalt", MapColor.STONE, 3.0F);
    public static final DeferredBlock<Block> COBBLED_BLACKSTONE = cobbled("cobbled_blackstone", MapColor.STONE, 3.0F);
    public static final DeferredBlock<Block> COBBLED_END_STONE = cobbled("cobbled_end_stone", MapColor.STONE, 2.0F);

    public static final DeferredBlock<LooseRocksBlock> LOOSE_ROCKS =
            BLOCKS.registerBlock(
                    "loose_rocks",
                    LooseRocksBlock::new,
                    properties -> properties
                            .mapColor(MapColor.STONE)
                            .replaceable()
                            .noCollision()
                            .strength(0.2F)
                            .sound(SoundType.STONE)
                            .pushReaction(PushReaction.DESTROY)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
            );

    public static final DeferredBlock<ExternalLooseRocksBlock>
            EXTERNAL_LOOSE_ROCKS = BLOCKS.registerBlock(
                    "external_loose_rocks",
                    ExternalLooseRocksBlock::new,
                    properties -> properties
                            .mapColor(MapColor.STONE)
                            .replaceable()
                            .noCollision()
                            .strength(0.2F)
                            .sound(SoundType.STONE)
                            .pushReaction(PushReaction.DESTROY)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
            );

    public static final DeferredBlock<GroundResourceBlock> GROUND_STICK =
            BLOCKS.registerBlock(
                    "ground_stick",
                    GroundResourceBlock::new,
                    properties -> properties
                            .mapColor(MapColor.WOOD)
                            .replaceable()
                            .noCollision()
                            .strength(0.2F)
                            .sound(SoundType.WOOD)
                            .pushReaction(PushReaction.DESTROY)
                            .offsetType(BlockBehaviour.OffsetType.XZ)
            );

    private ModBlocks() {
    }

    private static DeferredBlock<Block> cobbled(
            String name, MapColor mapColor, float strength
    ) {
        return BLOCKS.registerSimpleBlock(
                name,
                properties -> properties
                        .mapColor(mapColor)
                        .requiresCorrectToolForDrops()
                        .strength(strength)
        );
    }

    private static DeferredBlock<Block> oreBlock(String id) {
        boolean gravel = id.startsWith("gravel_");
        boolean deep = id.startsWith("deepslate_");
        return BLOCKS.registerSimpleBlock(
                id,
                properties -> properties
                        .mapColor(gravel ? MapColor.STONE : deep ? MapColor.DEEPSLATE : MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(gravel ? 0.8F : deep ? 4.5F : 3.0F, 3.0F)
                        .sound(gravel ? SoundType.GRAVEL : SoundType.STONE)
        );
    }

    /** Returns the registered visible form for an ore/host identifier. */
    public static DeferredBlock<Block> ore(String id) {
        DeferredBlock<Block> block = ORE_BLOCKS.get(id);
        if (block == null) {
            throw new IllegalArgumentException("Unknown Material Progression ore block: " + id);
        }
        return block;
    }

    public static Collection<Map.Entry<String, DeferredBlock<Block>>> oreBlocks() {
        return Map.copyOf(ORE_BLOCKS).entrySet();
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
