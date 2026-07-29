package dev.fishraposo.materialprogression.planner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CraftingPlannerTest {
    private static final PlanRequest.Limits DEFAULT_LIMITS =
            new PlanRequest.Limits(16, 256, 64);

    @Test
    void expandsLogsThroughPlanksAndSticksIntoTheFinalRecipe() {
        CraftingPlanner planner = planner(
                recipe("planks", "plank", 4, ingredient("log")),
                recipe("sticks", "stick", 4, ingredient("plank", 2)),
                recipe("ladder", "ladder", 3, ingredient("stick", 7))
        );

        CraftingPlan plan = planner.plan(request(
                "ladder",
                3,
                Map.of("log", 1)
        ));

        assertTrue(plan.successful());
        assertEquals(Map.of("log", 1), plan.consumed());
        assertEquals(
                List.of("planks", "sticks", "ladder"),
                plan.steps().stream()
                        .map(CraftingPlan.Step::recipeId)
                        .toList()
        );
        assertEquals(1, plan.surplus().get("stick"));
    }

    @Test
    void reusesExistingIntermediatesBeforeExpandingTheirRecipes() {
        CraftingPlanner planner = planner(
                recipe("planks", "plank", 4, ingredient("log")),
                recipe("sticks", "stick", 4, ingredient("plank", 2)),
                recipe("torch", "torch", 4,
                        ingredient("stick"),
                        ingredient("coal"))
        );

        CraftingPlan plan = planner.plan(request(
                "torch",
                4,
                Map.of("stick", 1, "coal", 1)
        ));

        assertTrue(plan.successful());
        assertEquals(Map.of("coal", 1, "stick", 1), plan.consumed());
        assertTrue(plan.steps().stream()
                .noneMatch(step -> step.recipeId().equals("planks")));
        assertTrue(plan.steps().stream()
                .noneMatch(step -> step.recipeId().equals("sticks")));
    }

    @Test
    void retainsUnavoidableRecipeBatchSurplus() {
        CraftingPlanner planner = planner(
                recipe("buttons", "button", 4, ingredient("plank"))
        );

        CraftingPlan plan = planner.plan(request(
                "button",
                5,
                Map.of("plank", 2)
        ));

        assertTrue(plan.successful());
        assertEquals(2, plan.crafts("buttons"));
        assertEquals(3, plan.surplus().get("button"));
    }

    @Test
    void prefersAnAlreadyAvailableTagAlternative() {
        CraftingPlanner planner = planner(
                recipe(
                        "chest",
                        "chest",
                        1,
                        ingredient(8, "oak_plank", "birch_plank")
                )
        );

        CraftingPlan plan = planner.plan(request(
                "chest",
                1,
                Map.of("birch_plank", 8, "oak_plank", 7)
        ));

        assertTrue(plan.successful());
        assertEquals(Map.of("birch_plank", 8), plan.consumed());
    }

    @Test
    void addsContainerRemaindersToSurplus() {
        CraftingPlanner planner = planner(new RecipeGraph.Recipe(
                "cake",
                new RecipeGraph.ItemAmount("cake", 1),
                List.of(ingredient("milk_bucket")),
                List.of(new RecipeGraph.ItemAmount("bucket", 1))
        ));

        CraftingPlan plan = planner.plan(request(
                "cake",
                1,
                Map.of("milk_bucket", 1)
        ));

        assertTrue(plan.successful());
        assertEquals(1, plan.surplus().get("bucket"));
    }

    @Test
    void choosesAStableSuccessfulAlternativeRecipe() {
        CraftingPlanner planner = planner(
                recipe("z_from_diamond", "gear", 1, ingredient("diamond")),
                recipe("a_from_iron", "gear", 1, ingredient("iron"))
        );

        CraftingPlan plan = planner.plan(request(
                "gear",
                1,
                Map.of("iron", 1, "diamond", 1)
        ));

        assertTrue(plan.successful());
        assertEquals("a_from_iron", plan.steps().getLast().recipeId());
        assertEquals(Map.of("iron", 1), plan.consumed());
    }

    @Test
    void reportsEveryMissingBaseIngredientWithoutMutation() {
        Map<String, Integer> inventory = Map.of("stick", 1);
        CraftingPlanner planner = planner(
                recipe("pickaxe", "pickaxe", 1,
                        ingredient("stick", 2),
                        ingredient("plank", 3))
        );

        CraftingPlan plan = planner.plan(request(
                "pickaxe",
                1,
                inventory
        ));

        assertFalse(plan.successful());
        assertEquals(
                Map.of("stick", 1, "plank", 3),
                plan.failure().orElseThrow().missing()
        );
        assertEquals(Map.of("stick", 1), inventory);
    }

    @Test
    void reportsDepthNodeAndQuantityLimitsExplicitly() {
        CraftingPlanner planner = planner(
                recipe("a", "a", 1, ingredient("b")),
                recipe("b", "b", 1, ingredient("c")),
                recipe("c", "c", 1, ingredient("ore"))
        );

        CraftingPlan depth = planner.plan(new PlanRequest(
                "a",
                1,
                Map.of("ore", 1),
                new PlanRequest.Limits(1, 256, 64)
        ));
        CraftingPlan nodes = planner.plan(new PlanRequest(
                "a",
                1,
                Map.of("ore", 1),
                new PlanRequest.Limits(16, 1, 64)
        ));
        CraftingPlan quantity = planner.plan(new PlanRequest(
                "a",
                65,
                Map.of("ore", 65),
                DEFAULT_LIMITS
        ));

        assertEquals(
                PlanningFailure.Reason.DEPTH_LIMIT,
                depth.failure().orElseThrow().reason()
        );
        assertEquals(
                PlanningFailure.Reason.NODE_LIMIT,
                nodes.failure().orElseThrow().reason()
        );
        assertEquals(
                PlanningFailure.Reason.QUANTITY_LIMIT,
                quantity.failure().orElseThrow().reason()
        );
    }

    @Test
    void detectsCyclesWithoutRecursingForever() {
        CraftingPlanner planner = planner(
                recipe("a_from_b", "a", 1, ingredient("b")),
                recipe("b_from_a", "b", 1, ingredient("a"))
        );

        CraftingPlan plan = planner.plan(request("a", 1, Map.of()));

        assertFalse(plan.successful());
        assertEquals(
                PlanningFailure.Reason.CYCLE,
                plan.failure().orElseThrow().reason()
        );
        assertEquals(List.of("a", "b", "a"),
                plan.failure().orElseThrow().path());
    }

    private static CraftingPlanner planner(RecipeGraph.Recipe... recipes) {
        return new CraftingPlanner(new RecipeGraph(List.of(recipes)));
    }

    private static PlanRequest request(
            String target,
            int quantity,
            Map<String, Integer> available
    ) {
        return new PlanRequest(target, quantity, available, DEFAULT_LIMITS);
    }

    private static RecipeGraph.Recipe recipe(
            String id,
            String output,
            int outputCount,
            RecipeGraph.Ingredient... ingredients
    ) {
        return new RecipeGraph.Recipe(
                id,
                new RecipeGraph.ItemAmount(output, outputCount),
                List.of(ingredients),
                List.of()
        );
    }

    private static RecipeGraph.Ingredient ingredient(String item) {
        return ingredient(item, 1);
    }

    private static RecipeGraph.Ingredient ingredient(
            String item,
            int count
    ) {
        return new RecipeGraph.Ingredient(List.of(item), count);
    }

    private static RecipeGraph.Ingredient ingredient(
            int count,
            String... alternatives
    ) {
        return new RecipeGraph.Ingredient(List.of(alternatives), count);
    }
}
