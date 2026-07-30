package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.world.level.block.entity.ManualWorkshopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class WorkshopFixture {
    static final BlockPos POSITION = GameTestSupport.DEFAULT_BLOCK_POS;

    private final ExtendedGameTestHelper helper;
    private final ManualWorkshopBlockEntity entity;

    private WorkshopFixture(
            ExtendedGameTestHelper helper,
            ManualWorkshopBlockEntity entity
    ) {
        this.helper = helper;
        this.entity = entity;
    }

    static WorkshopFixture place(ExtendedGameTestHelper helper) {
        return placeAt(helper, POSITION);
    }

    static WorkshopFixture placeAt(
            ExtendedGameTestHelper helper,
            BlockPos position
    ) {
        return new WorkshopFixture(
                helper,
                GameTestSupport.placeBlockEntity(
                        helper,
                        position,
                        ModBlocks.MANUAL_WORKSHOP.get(),
                        ManualWorkshopBlockEntity.class
                )
        );
    }

    static WorkshopFixture attach(
            ExtendedGameTestHelper helper,
            ManualWorkshopBlockEntity entity
    ) {
        helper.getLevel().removeBlockEntity(entity.getBlockPos());
        helper.getLevel().setBlockEntity(entity);
        return new WorkshopFixture(helper, entity);
    }

    ManualWorkshopBlockEntity entity() {
        return entity;
    }

    WorkshopFixture tool(Item item) {
        return tool(item.getDefaultInstance());
    }

    WorkshopFixture tool(ItemStack stack) {
        entity.setItem(
                ManualWorkshopBlockEntity.TOOL_SLOT,
                stack
        );
        return this;
    }

    WorkshopFixture input(Item item) {
        return input(new ItemStack(item));
    }

    WorkshopFixture input(ItemStack stack) {
        entity.setItem(
                ManualWorkshopBlockEntity.INPUT_SLOT,
                stack
        );
        return this;
    }

    WorkshopFixture output(ItemStack stack) {
        entity.setItem(
                ManualWorkshopBlockEntity.OUTPUT_SLOT,
                stack
        );
        return this;
    }

    ItemStack tool() {
        return entity.getItem(ManualWorkshopBlockEntity.TOOL_SLOT);
    }

    ItemStack input() {
        return entity.getItem(ManualWorkshopBlockEntity.INPUT_SLOT);
    }

    ItemStack output() {
        return entity.getItem(ManualWorkshopBlockEntity.OUTPUT_SLOT);
    }

    int progress() {
        return entity.progress();
    }

    int maxProgress() {
        return entity.maxProgress();
    }

    void tick(int times) {
        for (int tick = 0; tick < times; tick++) {
            ManualWorkshopBlockEntity.serverTick(
                    helper.getLevel(),
                    entity.getBlockPos(),
                    entity.getBlockState(),
                    entity
            );
        }
    }

    void clear() {
        entity.clearContent();
    }

    void clearOutput() {
        entity.setItem(
                ManualWorkshopBlockEntity.OUTPUT_SLOT,
                ItemStack.EMPTY
        );
    }
}
