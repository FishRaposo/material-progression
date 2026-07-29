package dev.fishraposo.materialprogression.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Immutable, registry-independent snapshot of ordinary crafting recipes. */
public final class RecipeGraph {
    private final Map<String, List<Recipe>> recipesByOutput;

    public RecipeGraph(List<Recipe> recipes) {
        Map<String, List<Recipe>> indexed = new HashMap<>();
        recipes.stream()
                .sorted((left, right) -> left.id().compareTo(right.id()))
                .forEach(recipe -> indexed.computeIfAbsent(
                        recipe.output().item(),
                        ignored -> new ArrayList<>()
                ).add(recipe));
        indexed.replaceAll((item, values) -> List.copyOf(values));
        recipesByOutput = Collections.unmodifiableMap(indexed);
    }

    public List<Recipe> recipesFor(String item) {
        return recipesByOutput.getOrDefault(item, List.of());
    }

    public record ItemAmount(String item, int count) {
        public ItemAmount {
            if (item == null || item.isBlank()) {
                throw new IllegalArgumentException("Item ID cannot be blank");
            }
            if (count <= 0) {
                throw new IllegalArgumentException(
                        "Item amount must be positive"
                );
            }
        }
    }

    public record Ingredient(List<String> alternatives, int count) {
        public Ingredient {
            if (alternatives == null || alternatives.isEmpty()) {
                throw new IllegalArgumentException(
                        "Ingredient alternatives cannot be empty"
                );
            }
            alternatives = alternatives.stream()
                    .peek(item -> {
                        if (item == null || item.isBlank()) {
                            throw new IllegalArgumentException(
                                    "Ingredient item ID cannot be blank"
                            );
                        }
                    })
                    .distinct()
                    .sorted()
                    .toList();
            if (count <= 0) {
                throw new IllegalArgumentException(
                        "Ingredient count must be positive"
                );
            }
        }
    }

    public record Recipe(
            String id,
            ItemAmount output,
            List<Ingredient> ingredients,
            List<ItemAmount> remainders
    ) {
        public Recipe {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException(
                        "Recipe ID cannot be blank"
                );
            }
            if (output == null) {
                throw new IllegalArgumentException(
                        "Recipe output cannot be null"
                );
            }
            ingredients = List.copyOf(ingredients);
            remainders = List.copyOf(remainders);
        }
    }
}
