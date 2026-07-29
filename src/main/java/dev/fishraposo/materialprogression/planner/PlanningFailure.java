package dev.fishraposo.materialprogression.planner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Visible, immutable reason that a requested craft cannot be planned. */
public record PlanningFailure(
        Reason reason,
        Map<String, Integer> missing,
        List<String> path,
        String detail
) {
    public PlanningFailure {
        missing = Collections.unmodifiableMap(new TreeMap<>(missing));
        path = List.copyOf(path);
        detail = detail == null ? "" : detail;
    }

    public enum Reason {
        MISSING_INGREDIENT,
        CYCLE,
        DEPTH_LIMIT,
        NODE_LIMIT,
        QUANTITY_LIMIT
    }
}
