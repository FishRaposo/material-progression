package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.block.entity.BulkCraftingTableBlockEntity;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import dev.fishraposo.materialprogression.world.level.block.entity.WorkshopBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MaterialProgression.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER =
            BLOCK_ENTITIES.register(
                    "crusher",
                    () -> new BlockEntityType<>(CrusherBlockEntity::new, ModBlocks.CRUSHER.get())
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkshopBlockEntity>> WORKSHOP =
            BLOCK_ENTITIES.register(
                    "workshop",
                    () -> new BlockEntityType<>(
                            WorkshopBlockEntity::new,
                            ModBlocks.WORKSHOP.get()
                    )
            );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<BulkCraftingTableBlockEntity>
    > BULK_CRAFTING_TABLE = BLOCK_ENTITIES.register(
            "bulk_crafting_table",
            () -> new BlockEntityType<>(
                    BulkCraftingTableBlockEntity::new,
                    ModBlocks.BULK_CRAFTING_TABLE.get()
            )
    );

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
