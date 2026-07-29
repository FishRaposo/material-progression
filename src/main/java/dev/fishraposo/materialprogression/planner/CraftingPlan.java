package dev.fishraposo.materialprogression.planner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Immutable ordered recipe plan or explicit bounded-planning failure. */
public record CraftingPlan(
        List<Step> steps,
        Map<String, Integer> consumed,
        Map<String, Integer> surplus,
        Optional<PlanningFailure> failure
) {
    public CraftingPlan {
        steps = List.copyOf(steps);
        consumed = immutableCounts(consumed);
        surplus = immutableCounts(surplus);
        failure = failure == null ? Optional.empty() : failure;
    }

    public boolean successful() {
        return failure.isEmpty();
    }

    public int crafts(String recipeId) {
        return steps.stream()
                .filter(step -> step.recipeId().equals(recipeId))
                .mapToInt(Step::crafts)
                .sum();
    }

    static CraftingPlan success(
            List<Step> steps,
            Map<String, Integer> consumed,
            Map<String, Integer> surplus
    ) {
        return new CraftingPlan(
                steps,
                consumed,
                surplus,
                Optional.empty()
        );
    }

    static CraftingPlan failed(
            List<Step> steps,
            Map<String, Integer> consumed,
            Map<String, Integer> surplus,
            PlanningFailure failure
    ) {
        return new CraftingPlan(
                steps,
                consumed,
                surplus,
                Optional.of(failure)
        );
    }

    private static Map<String, Integer> immutableCounts(
            Map<String, Integer> counts
    ) {
        return Collections.unmodifiableMap(new TreeMap<>(counts));
    }

    public record Step(String recipeId, int crafts) {
        public Step {
            if (recipeId == null || recipeId.isBlank() || crafts <= 0) {
                throw new IllegalArgumentException(
                        "Crafting steps require an ID and positive count"
                );
            }
        }
    }
}
