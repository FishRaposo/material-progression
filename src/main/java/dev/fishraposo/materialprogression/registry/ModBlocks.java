package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.block.CrusherBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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

    public static final DeferredBlock<Block> TIN_ORE = BLOCKS.registerSimpleBlock(
            "tin_ore",
            properties -> properties
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F)
    );

    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = BLOCKS.registerSimpleBlock(
            "deepslate_tin_ore",
            properties -> properties
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 3.0F)
    );

    private ModBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
