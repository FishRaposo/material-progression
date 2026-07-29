package dev.fishraposo.materialprogression.planner;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** Immutable target, inventory snapshot, and safety bounds for one plan. */
public record PlanRequest(
        String target,
        int quantity,
        Map<String, Integer> available,
        Limits limits
) {
    public PlanRequest {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("Plan target cannot be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive"
            );
        }
        if (limits == null) {
            throw new IllegalArgumentException("Planner limits are required");
        }
        TreeMap<String, Integer> snapshot = new TreeMap<>();
        available.forEach((item, count) -> {
            if (item == null || item.isBlank() || count == null || count < 0) {
                throw new IllegalArgumentException(
                        "Inventory snapshots require nonnegative item counts"
                );
            }
            if (count > 0) {
                snapshot.put(item, count);
            }
        });
        available = Collections.unmodifiableMap(snapshot);
    }

    public record Limits(int maxDepth, int maxNodes, int maxQuantity) {
        public Limits {
            if (maxDepth < 0 || maxNodes <= 0 || maxQuantity <= 0) {
                throw new IllegalArgumentException(
                        "Planner limits must be nonnegative and bounded"
                );
            }
        }
    }
}
