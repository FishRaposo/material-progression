package dev.fishraposo.materialprogression.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ManualWorkshopRecipeInput(
        ItemStack tool,
        ItemStack ingredient
) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> tool;
            case 1 -> ingredient;
            default -> throw new IllegalArgumentException(
                    "Manual workshop recipe does not contain slot " + index
            );
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return tool.isEmpty() && ingredient.isEmpty();
    }
}
