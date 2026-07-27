package dev.fishraposo.materialprogression.world.inventory;

import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;

public final class CrusherMenu extends AbstractFurnaceMenu {
    public CrusherMenu(int containerId, Inventory inventory) {
        super(
                ModMenus.CRUSHER.get(),
                ModRecipes.CRUSHING.get(),
                RecipePropertySet.FURNACE_INPUT,
                RecipeBookType.FURNACE,
                containerId,
                inventory
        );
    }

    public CrusherMenu(
            int containerId,
            Inventory inventory,
            Container container,
            ContainerData data
    ) {
        super(
                ModMenus.CRUSHER.get(),
                ModRecipes.CRUSHING.get(),
                RecipePropertySet.FURNACE_INPUT,
                RecipeBookType.FURNACE,
                containerId,
                inventory,
                container,
                data
        );
    }

    @Override
    protected boolean canSmelt(ItemStack stack) {
        return stack.is(ModTags.CRUSHER_INPUTS);
    }
}
