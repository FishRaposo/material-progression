package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
import dev.fishraposo.materialprogression.world.level.block.entity.ManualWorkshopBlockEntity;
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

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<ManualWorkshopBlockEntity>
    > MANUAL_WORKSHOP = BLOCK_ENTITIES.register(
            "manual_workshop",
            () -> new BlockEntityType<>(
                    ManualWorkshopBlockEntity::new,
                    ModBlocks.MANUAL_WORKSHOP.get()
            )
    );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<ExternalLooseRockBlockEntity>
    > EXTERNAL_LOOSE_ROCKS = BLOCK_ENTITIES.register(
            "external_loose_rocks",
            () -> new BlockEntityType<>(
                    ExternalLooseRockBlockEntity::new,
                    ModBlocks.EXTERNAL_LOOSE_ROCKS.get()
            )
    );

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
