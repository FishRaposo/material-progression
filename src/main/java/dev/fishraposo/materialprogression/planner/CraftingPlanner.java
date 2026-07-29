package dev.fishraposo.materialprogression.planner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Deterministic bounded recursive planner over a {@link RecipeGraph}. */
public final class CraftingPlanner {
    private final RecipeGraph graph;

    public CraftingPlanner(RecipeGraph graph) {
        this.graph = graph;
    }

    public CraftingPlan plan(PlanRequest request) {
        if (request.quantity() > request.limits().maxQuantity()) {
            return CraftingPlan.failed(
                    List.of(),
                    Map.of(),
                    Map.of(),
                    new PlanningFailure(
                            PlanningFailure.Reason.QUANTITY_LIMIT,
                            Map.of(),
                            List.of(request.target()),
                            "Requested quantity exceeds the configured limit"
                    )
            );
        }

        State state = new State(request.available());
        boolean successful = acquire(
                state,
                request.target(),
                request.quantity(),
                0,
                List.of(),
                request.limits()
        );
        List<CraftingPlan.Step> steps = state.steps.entrySet().stream()
                .map(entry -> new CraftingPlan.Step(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
        if (successful) {
            return CraftingPlan.success(
                    steps,
                    state.consumed,
                    state.crafted
            );
        }
        PlanningFailure failure = new PlanningFailure(
                state.failureReason == null
                        ? PlanningFailure.Reason.MISSING_INGREDIENT
                        : state.failureReason,
                state.missing,
                state.failurePath,
                state.failureDetail
        );
        return CraftingPlan.failed(
                steps,
                state.consumed,
                state.crafted,
                failure
        );
    }

    private boolean acquire(
            State state,
            String item,
            int amount,
            int depth,
            List<String> path,
            PlanRequest.Limits limits
    ) {
        if (depth > limits.maxDepth()) {
            state.recordFailure(
                    PlanningFailure.Reason.DEPTH_LIMIT,
                    append(path, item),
                    "Recipe depth exceeded " + limits.maxDepth()
            );
            return false;
        }
        state.expandedNodes++;
        if (state.expandedNodes > limits.maxNodes()) {
            state.recordFailure(
                    PlanningFailure.Reason.NODE_LIMIT,
                    append(path, item),
                    "Expanded node count exceeded " + limits.maxNodes()
            );
            return false;
        }

        int remaining = state.take(item, amount);
        if (remaining == 0) {
            return true;
        }
        if (path.contains(item)) {
            state.recordFailure(
                    PlanningFailure.Reason.CYCLE,
                    append(path, item),
                    "Recipe cycle detected"
            );
            return false;
        }

        List<RecipeGraph.Recipe> recipes = graph.recipesFor(item);
        if (recipes.isEmpty()) {
            state.missing.merge(item, remaining, Integer::sum);
            state.recordFailure(
                    PlanningFailure.Reason.MISSING_INGREDIENT,
                    append(path, item),
                    "No crafting recipe supplies " + item
            );
            return false;
        }

        State base = state.copy();
        State bestFailure = null;
        List<String> nextPath = append(path, item);
        for (RecipeGraph.Recipe recipe : recipes) {
            State candidate = base.copy();
            if (craft(
                    candidate,
                    recipe,
                    remaining,
                    depth,
                    nextPath,
                    limits
            )) {
                state.replaceWith(candidate);
                return true;
            }
            if (bestFailure == null
                    || candidate.failureScore() < bestFailure.failureScore()) {
                bestFailure = candidate;
            }
        }
        state.replaceWith(bestFailure == null ? base : bestFailure);
        return false;
    }

    private boolean craft(
            State state,
            RecipeGraph.Recipe recipe,
            int requiredOutput,
            int depth,
            List<String> path,
            PlanRequest.Limits limits
    ) {
        int crafts = divideRoundUp(
                requiredOutput,
                recipe.output().count()
        );
        boolean complete = true;
        for (RecipeGraph.Ingredient ingredient : recipe.ingredients()) {
            if (!acquireIngredient(
                    state,
                    ingredient,
                    ingredient.count() * crafts,
                    depth + 1,
                    path,
                    limits
            )) {
                complete = false;
            }
        }
        if (!complete) {
            return false;
        }

        state.steps.merge(recipe.id(), crafts, Integer::sum);
        state.crafted.merge(
                recipe.output().item(),
                recipe.output().count() * crafts,
                Integer::sum
        );
        recipe.remainders().forEach(remainder -> state.crafted.merge(
                remainder.item(),
                remainder.count() * crafts,
                Integer::sum
        ));
        return state.take(recipe.output().item(), requiredOutput) == 0;
    }

    private boolean acquireIngredient(
            State state,
            RecipeGraph.Ingredient ingredient,
            int amount,
            int depth,
            List<String> path,
            PlanRequest.Limits limits
    ) {
        List<String> alternatives = ingredient.alternatives().stream()
                .sorted(Comparator
                        .comparingInt(
                                (String item) ->
                                        state.available(item) >= amount ? 0 : 1
                        )
                        .thenComparing(Comparator.naturalOrder()))
                .toList();
        State bestFailure = null;
        for (String alternative : alternatives) {
            State candidate = state.copy();
            if (acquire(
                    candidate,
                    alternative,
                    amount,
                    depth,
                    path,
                    limits
            )) {
                state.replaceWith(candidate);
                return true;
            }
            if (bestFailure == null
                    || candidate.failureScore() < bestFailure.failureScore()) {
                bestFailure = candidate;
            }
        }
        if (bestFailure != null) {
            state.replaceWith(bestFailure);
        }
        return false;
    }

    private static int divideRoundUp(int value, int divisor) {
        return (value + divisor - 1) / divisor;
    }

    private static List<String> append(List<String> path, String item) {
        List<String> result = new ArrayList<>(path.size() + 1);
        result.addAll(path);
        result.add(item);
        return List.copyOf(result);
    }

    private static final class State {
        private final Map<String, Integer> initial = new TreeMap<>();
        private final Map<String, Integer> crafted = new TreeMap<>();
        private final Map<String, Integer> consumed = new TreeMap<>();
        private final Map<String, Integer> missing = new TreeMap<>();
        private final Map<String, Integer> steps = new LinkedHashMap<>();
        private int expandedNodes;
        private PlanningFailure.Reason failureReason;
        private List<String> failurePath = List.of();
        private String failureDetail = "";

        private State(Map<String, Integer> available) {
            initial.putAll(available);
        }

        private State(State source) {
            replaceWith(source);
        }

        private State copy() {
            return new State(this);
        }

        private void replaceWith(State source) {
            replace(initial, source.initial);
            replace(crafted, source.crafted);
            replace(consumed, source.consumed);
            replace(missing, source.missing);
            replace(steps, source.steps);
            expandedNodes = source.expandedNodes;
            failureReason = source.failureReason;
            failurePath = source.failurePath;
            failureDetail = source.failureDetail;
        }

        private int take(String item, int amount) {
            int remaining = takeFrom(initial, item, amount);
            int usedInitial = amount - remaining;
            if (usedInitial > 0) {
                consumed.merge(item, usedInitial, Integer::sum);
            }
            return takeFrom(crafted, item, remaining);
        }

        private int available(String item) {
            return initial.getOrDefault(item, 0)
                    + crafted.getOrDefault(item, 0);
        }

        private void recordFailure(
                PlanningFailure.Reason reason,
                List<String> path,
                String detail
        ) {
            if (failureReason == null
                    || failureReason
                            == PlanningFailure.Reason.MISSING_INGREDIENT
                    && reason != PlanningFailure.Reason.MISSING_INGREDIENT) {
                failureReason = reason;
                failurePath = path;
                failureDetail = detail;
            }
        }

        private int failureScore() {
            int missingCount = missing.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
            return failureReason == PlanningFailure.Reason.CYCLE
                    || failureReason == PlanningFailure.Reason.DEPTH_LIMIT
                    || failureReason == PlanningFailure.Reason.NODE_LIMIT
                    ? missingCount
                    : 1_000_000 + missingCount;
        }

        private static int takeFrom(
                Map<String, Integer> stock,
                String item,
                int amount
        ) {
            int available = stock.getOrDefault(item, 0);
            int taken = Math.min(available, amount);
            if (taken == available) {
                stock.remove(item);
            } else if (taken > 0) {
                stock.put(item, available - taken);
            }
            return amount - taken;
        }

        private static void replace(
                Map<String, Integer> target,
                Map<String, Integer> source
        ) {
            target.clear();
            target.putAll(source);
        }
    }
}
