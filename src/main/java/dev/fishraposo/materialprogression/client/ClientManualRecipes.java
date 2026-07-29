package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

/** Client copy of the manual recipes explicitly synchronized by NeoForge. */
public final class ClientManualRecipes {
    private static List<RecipeHolder<ManualProcessingRecipe>> recipes =
            List.of();

    private ClientManualRecipes() {
    }

    public static void replace(RecipeMap recipeMap) {
        recipes = recipeMap.byType(ModRecipes.MANUAL_PROCESSING.get())
                .stream()
                .sorted(Comparator.comparing(
                        holder -> holder.id().identifier().toString()
                ))
                .toList();
    }

    public static void clear() {
        recipes = List.of();
    }

    public static List<RecipeHolder<ManualProcessingRecipe>> matching(
            ItemStack tool,
            ItemStack input
    ) {
        return recipes.stream()
                .filter(holder -> holder.value().matches(tool, input))
                .toList();
    }
}
