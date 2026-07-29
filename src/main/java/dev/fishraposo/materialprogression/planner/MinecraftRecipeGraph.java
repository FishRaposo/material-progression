package dev.fishraposo.materialprogression.planner;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.server.level.ServerLevel;

/** Builds the planner's immutable graph from server-authoritative recipes. */
public final class MinecraftRecipeGraph {
    private MinecraftRecipeGraph() {
    }

    public static RecipeGraph snapshot(ServerLevel level) {
        List<RecipeGraph.Recipe> recipes = new ArrayList<>();
        level.recipeAccess()
                .recipeMap()
                .byType(RecipeType.CRAFTING)
                .stream()
                .sorted(java.util.Comparator.comparing(
                        holder -> holder.id().identifier().toString()
                ))
                .map(holder -> adapt(holder, level))
                .flatMap(java.util.Optional::stream)
                .forEach(recipes::add);
        return new RecipeGraph(recipes);
    }

    static java.util.Optional<RecipeGraph.Recipe> adapt(
            RecipeHolder<CraftingRecipe> holder,
            ServerLevel level
    ) {
        CraftingRecipe recipe = holder.value();
        PlacementInfo placement = recipe.placementInfo();
        if (recipe.isSpecial() || placement.isImpossibleToPlace()) {
            return java.util.Optional.empty();
        }
        List<Ingredient> ingredients = placement.ingredients();
        if (ingredients.isEmpty()
                || ingredients.stream().anyMatch(Ingredient::isCustom)) {
            return java.util.Optional.empty();
        }

        List<ItemStack> inputStacks = representativeInput(placement);
        if (inputStacks.isEmpty()) {
            return java.util.Optional.empty();
        }
        CraftingInput input = matchingInput(recipe, level, inputStacks);
        if (input == null) {
            return java.util.Optional.empty();
        }
        ItemStack output = recipe.assemble(input);
        if (output.isEmpty()) {
            return java.util.Optional.empty();
        }

        Map<List<String>, Integer> counts = new LinkedHashMap<>();
        IntList slots = placement.slotsToIngredientIndex();
        for (int slot = 0; slot < slots.size(); slot++) {
            int ingredientIndex = slots.getInt(slot);
            if (ingredientIndex >= 0) {
                List<String> alternatives = alternatives(
                        ingredients.get(ingredientIndex)
                );
                if (alternatives.isEmpty()) {
                    return java.util.Optional.empty();
                }
                counts.merge(alternatives, 1, Integer::sum);
            }
        }
        List<RecipeGraph.Ingredient> graphIngredients =
                counts.entrySet().stream()
                        .map(entry -> new RecipeGraph.Ingredient(
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();

        Map<String, Integer> remainders = new TreeMap<>();
        recipe.getRemainingItems(input).stream()
                .filter(stack -> !stack.isEmpty())
                .forEach(stack -> remainders.merge(
                        itemId(stack),
                        stack.getCount(),
                        Integer::sum
                ));
        List<RecipeGraph.ItemAmount> graphRemainders =
                remainders.entrySet().stream()
                        .map(entry -> new RecipeGraph.ItemAmount(
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();

        return java.util.Optional.of(new RecipeGraph.Recipe(
                holder.id().identifier().toString(),
                new RecipeGraph.ItemAmount(
                        itemId(output),
                        output.getCount()
                ),
                graphIngredients,
                graphRemainders
        ));
    }

    private static List<ItemStack> representativeInput(
            PlacementInfo placement
    ) {
        List<Ingredient> ingredients = placement.ingredients();
        List<ItemStack> result = new ArrayList<>();
        IntList slots = placement.slotsToIngredientIndex();
        for (int slot = 0; slot < slots.size(); slot++) {
            int index = slots.getInt(slot);
            if (index < 0) {
                result.add(ItemStack.EMPTY);
                continue;
            }
            Holder<Item> first = ingredients.get(index)
                    .items()
                    .findFirst()
                    .orElse(null);
            if (first == null) {
                return List.of();
            }
            result.add(new ItemStack(first.value()));
        }
        return List.copyOf(result);
    }

    private static List<String> alternatives(Ingredient ingredient) {
        return ingredient.items()
                .map(holder ->
                        BuiltInRegistries.ITEM.getKey(
                                holder.value()
                        ).toString())
                .distinct()
                .sorted()
                .toList();
    }

    private static CraftingInput matchingInput(
            CraftingRecipe recipe,
            ServerLevel level,
            List<ItemStack> stacks
    ) {
        for (int width = 1; width <= 3; width++) {
            if (stacks.size() % width != 0) {
                continue;
            }
            int height = stacks.size() / width;
            if (height < 1 || height > 3) {
                continue;
            }
            CraftingInput input = CraftingInput.of(
                    width,
                    height,
                    stacks
            );
            if (recipe.matches(input, level)) {
                return input;
            }
        }
        return null;
    }

    private static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
