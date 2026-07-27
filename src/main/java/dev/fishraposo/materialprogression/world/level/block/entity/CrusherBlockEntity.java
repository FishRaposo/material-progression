package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.world.inventory.CrusherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class CrusherBlockEntity extends AbstractFurnaceBlockEntity {
    private static final Component DEFAULT_NAME =
            Component.translatable("container.material_progression.crusher");

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER.get(), pos, state, ModRecipes.CRUSHING.get());
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new CrusherMenu(containerId, inventory, this, this.dataAccess);
    }
}
