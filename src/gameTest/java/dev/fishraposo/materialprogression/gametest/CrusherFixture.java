package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class CrusherFixture {
    static final int INPUT_SLOT = 0;
    static final int FUEL_SLOT = 1;
    static final int OUTPUT_SLOT = 2;
    static final BlockPos POSITION = GameTestSupport.DEFAULT_BLOCK_POS;

    private final CrusherBlockEntity entity;

    private CrusherFixture(CrusherBlockEntity entity) {
        this.entity = entity;
    }

    static CrusherFixture place(ExtendedGameTestHelper helper) {
        return new CrusherFixture(GameTestSupport.placeBlockEntity(
                helper,
                POSITION,
                ModBlocks.CRUSHER.get(),
                CrusherBlockEntity.class
        ));
    }

    CrusherBlockEntity entity() {
        return entity;
    }

    CrusherFixture input(Item item) {
        entity.setItem(INPUT_SLOT, item.getDefaultInstance());
        return this;
    }

    CrusherFixture fuel(Item item) {
        entity.setItem(FUEL_SLOT, item.getDefaultInstance());
        return this;
    }

    ItemStack input() {
        return entity.getItem(INPUT_SLOT);
    }

    ItemStack fuel() {
        return entity.getItem(FUEL_SLOT);
    }

    ItemStack output() {
        return entity.getItem(OUTPUT_SLOT);
    }

    void clearOutput() {
        entity.setItem(OUTPUT_SLOT, ItemStack.EMPTY);
    }
}
