package dev.fishraposo.materialprogression.transaction;

import dev.fishraposo.materialprogression.planner.CraftingPlan;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Applies a completed crafting plan across ordered inventory slices.
 *
 * <p>The same immutable snapshot is used for preview and commit. Inputs are
 * consumed in slice/slot order, outputs go only to the declared output slice,
 * and any failed write restores every previously touched slot.</p>
 */
public final class BulkCraftingTransaction {
    private final List<Slice> sources;
    private final Slice output;

    public BulkCraftingTransaction(List<Slice> sources, Slice output) {
        if (sources == null || sources.isEmpty() || output == null) {
            throw new IllegalArgumentException(
                    "Bulk crafting requires sources and an output"
            );
        }
        this.sources = List.copyOf(sources);
        this.output = output;
    }

    public Preview simulate(
            CraftingPlan plan,
            String target,
            int quantity
    ) {
        if (!plan.successful() || target == null || target.isBlank()
                || quantity <= 0) {
            return Preview.rejected("invalid_plan");
        }

        List<InventoryState> states = snapshotViews();
        Map<InventoryView, InventoryState> byView =
                new IdentityHashMap<>();
        states.forEach(state -> byView.put(state.view, state));

        for (Map.Entry<String, Integer> cost
                : plan.consumed().entrySet()) {
            int remaining = consume(
                    byView,
                    cost.getKey(),
                    cost.getValue()
            );
            if (remaining > 0) {
                return Preview.rejected("inventory_changed");
            }
        }

        Map<String, Integer> produced = new TreeMap<>(plan.surplus());
        produced.merge(target, quantity, Integer::sum);
        for (Map.Entry<String, Integer> result : produced.entrySet()) {
            if (!insert(
                    byView.get(output.view),
                    output.slots,
                    result.getKey(),
                    result.getValue()
            )) {
                return Preview.rejected("insufficient_output_capacity");
            }
        }

        List<ViewDelta> deltas = states.stream()
                .map(InventoryState::delta)
                .filter(delta -> !delta.slots().isEmpty())
                .toList();
        return new Preview(
                true,
                "",
                fingerprint(states),
                deltas
        );
    }

    public boolean commit(Preview preview) {
        if (!preview.accepted) {
            return false;
        }
        List<InventoryState> current = snapshotViews();
        if (fingerprint(current) != preview.fingerprint
                || !preview.matchesExpected(current)) {
            return false;
        }
        for (ViewDelta viewDelta : preview.deltas) {
            for (SlotDelta slot : viewDelta.slots) {
                if (!viewDelta.view.canStore(slot.slot, slot.after)) {
                    return false;
                }
            }
        }

        List<WrittenSlot> written = new ArrayList<>();
        try {
            for (ViewDelta viewDelta : preview.deltas) {
                for (SlotDelta slot : viewDelta.slots) {
                    written.add(new WrittenSlot(
                            viewDelta.view,
                            slot.slot,
                            slot.before
                    ));
                    viewDelta.view.setItem(slot.slot, slot.after);
                }
            }
            return true;
        } catch (RuntimeException exception) {
            for (int index = written.size() - 1; index >= 0; index--) {
                WrittenSlot slot = written.get(index);
                try {
                    slot.view.setItem(slot.slot, slot.before);
                } catch (RuntimeException ignored) {
                    // InventoryView guarantees that accepted prior state restores.
                }
            }
            return false;
        }
    }

    private List<InventoryState> snapshotViews() {
        Map<InventoryView, InventoryState> states =
                new IdentityHashMap<>();
        for (Slice source : sources) {
            states.computeIfAbsent(
                    source.view,
                    InventoryState::capture
            );
        }
        states.computeIfAbsent(output.view, InventoryState::capture);

        List<InventoryState> ordered = new ArrayList<>();
        for (Slice source : sources) {
            InventoryState state = states.get(source.view);
            if (!ordered.contains(state)) {
                ordered.add(state);
            }
        }
        InventoryState outputState = states.get(output.view);
        if (!ordered.contains(outputState)) {
            ordered.add(outputState);
        }
        return List.copyOf(ordered);
    }

    private int consume(
            Map<InventoryView, InventoryState> states,
            String item,
            int amount
    ) {
        int remaining = amount;
        for (Slice source : sources) {
            InventoryState state = states.get(source.view);
            for (int slot : source.slots) {
                ItemStack stack = state.after.get(slot);
                if (itemId(stack).equals(item)) {
                    int taken = Math.min(stack.getCount(), remaining);
                    stack.shrink(taken);
                    remaining -= taken;
                }
                if (remaining == 0) {
                    return 0;
                }
            }
        }
        return remaining;
    }

    private boolean insert(
            InventoryState state,
            List<Integer> slots,
            String item,
            int count
    ) {
        ItemStack remaining = stack(item, count);
        if (remaining.isEmpty()) {
            return false;
        }
        for (int slot : slots) {
            ItemStack existing = state.after.get(slot);
            if (ItemStack.isSameItemSameComponents(existing, remaining)) {
                moveInto(state, slot, existing, remaining);
            }
            if (remaining.isEmpty()) {
                return true;
            }
        }
        for (int slot : slots) {
            if (state.after.get(slot).isEmpty()) {
                moveInto(state, slot, ItemStack.EMPTY, remaining);
            }
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void moveInto(
            InventoryState state,
            int slot,
            ItemStack existing,
            ItemStack incoming
    ) {
        int maximum = Math.min(
                incoming.getCount(),
                incoming.getMaxStackSize() - existing.getCount()
        );
        for (int amount = maximum; amount > 0; amount--) {
            ItemStack candidate = existing.isEmpty()
                    ? incoming.copyWithCount(amount)
                    : existing.copyWithCount(existing.getCount() + amount);
            if (state.view.canStore(slot, candidate)) {
                state.after.set(slot, candidate);
                incoming.shrink(amount);
                return;
            }
        }
    }

    private static long fingerprint(List<InventoryState> states) {
        long hash = 0xcbf29ce484222325L;
        for (InventoryState state : states) {
            hash = mix(hash, state.revision);
            for (ItemStack stack : state.before) {
                hash = mix(hash, ItemStack.hashItemAndComponents(stack));
                hash = mix(hash, stack.getCount());
            }
        }
        return hash;
    }

    private static long mix(long hash, long value) {
        return (hash ^ value) * 0x100000001b3L;
    }

    private static String itemId(ItemStack stack) {
        return stack.isEmpty()
                ? ""
                : BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static ItemStack stack(String item, int count) {
        Identifier id = Identifier.tryParse(item);
        return id == null
                ? ItemStack.EMPTY
                : new ItemStack(BuiltInRegistries.ITEM.getValue(id), count);
    }

    public record Slice(InventoryView view, List<Integer> slots) {
        public Slice {
            if (view == null || slots == null || slots.isEmpty()) {
                throw new IllegalArgumentException(
                        "Inventory slices require a view and slots"
                );
            }
            slots = slots.stream().distinct().toList();
            for (int slot : slots) {
                if (slot < 0 || slot >= view.size()) {
                    throw new IllegalArgumentException(
                            "Inventory slice slot is out of bounds"
                    );
                }
            }
        }
    }

    public static final class Preview {
        private final boolean accepted;
        private final String failure;
        private final long fingerprint;
        private final List<ViewDelta> deltas;

        private Preview(
                boolean accepted,
                String failure,
                long fingerprint,
                List<ViewDelta> deltas
        ) {
            this.accepted = accepted;
            this.failure = failure;
            this.fingerprint = fingerprint;
            this.deltas = List.copyOf(deltas);
        }

        private static Preview rejected(String failure) {
            return new Preview(false, failure, 0, List.of());
        }

        public boolean accepted() {
            return accepted;
        }

        public String failure() {
            return failure;
        }

        public long fingerprint() {
            return fingerprint;
        }

        public List<ViewDelta> deltas() {
            return deltas;
        }

        private boolean matchesExpected(List<InventoryState> current) {
            Map<InventoryView, InventoryState> byView =
                    new IdentityHashMap<>();
            current.forEach(state -> byView.put(state.view, state));
            for (ViewDelta delta : deltas) {
                InventoryState state = byView.get(delta.view);
                if (state == null) {
                    return false;
                }
                for (SlotDelta slot : delta.slots) {
                    if (!sameStack(
                            slot.before,
                            state.before.get(slot.slot)
                    )) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public record ViewDelta(
            InventoryView view,
            List<SlotDelta> slots
    ) {
        public ViewDelta {
            slots = List.copyOf(slots);
        }
    }

    public record SlotDelta(
            int slot,
            ItemStack before,
            ItemStack after
    ) {
        public SlotDelta {
            before = before.copy();
            after = after.copy();
        }

        @Override
        public ItemStack before() {
            return before.copy();
        }

        @Override
        public ItemStack after() {
            return after.copy();
        }
    }

    private static final class InventoryState {
        private final InventoryView view;
        private final long revision;
        private final List<ItemStack> before;
        private final List<ItemStack> after;

        private InventoryState(
                InventoryView view,
                long revision,
                List<ItemStack> before,
                List<ItemStack> after
        ) {
            this.view = view;
            this.revision = revision;
            this.before = before;
            this.after = after;
        }

        private static InventoryState capture(InventoryView view) {
            List<ItemStack> snapshot = new ArrayList<>(view.size());
            for (int slot = 0; slot < view.size(); slot++) {
                snapshot.add(view.getItem(slot).copy());
            }
            return new InventoryState(
                    view,
                    view.revision(),
                    List.copyOf(snapshot),
                    copy(snapshot)
            );
        }

        private ViewDelta delta() {
            List<SlotDelta> slots = new ArrayList<>();
            for (int slot = 0; slot < before.size(); slot++) {
                if (!sameStack(before.get(slot), after.get(slot))) {
                    slots.add(new SlotDelta(
                            slot,
                            before.get(slot),
                            after.get(slot)
                    ));
                }
            }
            return new ViewDelta(view, slots);
        }

        private static List<ItemStack> copy(List<ItemStack> stacks) {
            return stacks.stream()
                    .map(ItemStack::copy)
                    .collect(
                            ArrayList::new,
                            ArrayList::add,
                            ArrayList::addAll
                    );
        }
    }

    private record WrittenSlot(
            InventoryView view,
            int slot,
            ItemStack before
    ) {
        private WrittenSlot {
            before = before.copy();
        }
    }

    private static boolean sameStack(ItemStack left, ItemStack right) {
        return left.getCount() == right.getCount()
                && ItemStack.isSameItemSameComponents(left, right);
    }
}
