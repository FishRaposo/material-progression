package dev.fishraposo.materialprogression.transaction;

import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/** Simulates one manual-processing operation and commits it only when unchanged. */
public final class ItemTransaction {
    private final InventoryView inventory;
    private final ManualProcessingRecipe recipe;
    private final int toolSlot;
    private final int inputSlot;
    private final List<Integer> outputSlots;

    private ItemTransaction(
            InventoryView inventory,
            ManualProcessingRecipe recipe,
            int toolSlot,
            int inputSlot,
            List<Integer> outputSlots
    ) {
        this.inventory = inventory;
        this.recipe = recipe;
        this.toolSlot = toolSlot;
        this.inputSlot = inputSlot;
        this.outputSlots = outputSlots.stream().sorted(Comparator.naturalOrder()).toList();
        validateSlots();
    }

    public static ItemTransaction manualProcessing(
            InventoryView inventory,
            ManualProcessingRecipe recipe,
            int toolSlot,
            int inputSlot,
            List<Integer> outputSlots
    ) {
        return new ItemTransaction(inventory, recipe, toolSlot, inputSlot, outputSlots);
    }

    public Preview simulate() {
        return simulateBatch(1).transactionPreview();
    }

    public OperationPreview simulateBatch(int requested) {
        if (requested <= 0 || requested > 64) {
            throw new IllegalArgumentException(
                    "Manual-processing batch size must be between 1 and 64"
            );
        }
        List<ItemStack> before = snapshot();
        List<ItemStack> after = copyStacks(before);
        ItemStack originalInput = before.get(inputSlot);
        int executable = 0;
        String failure = "";
        ItemStackTemplate remainderTemplate = originalInput.getCraftingRemainder();

        while (executable < requested) {
            List<ItemStack> candidate = copyStacks(after);
            ItemStack tool = candidate.get(toolSlot);
            ItemStack input = candidate.get(inputSlot);
            if (!recipe.matches(tool, input)) {
                failure = "tool_or_input_mismatch";
                break;
            }
            if (!tool.isDamageableItem()
                    || tool.getMaxDamage() - tool.getDamageValue()
                    <= recipe.durabilityCost()) {
                failure = "insufficient_durability";
                break;
            }

            tool.setDamageValue(
                    tool.getDamageValue() + recipe.durabilityCost()
            );
            input.shrink(1);
            if (!insert(candidate, recipe.resultStack())
                    || !insert(
                            candidate,
                            remainderTemplate != null
                                    ? remainderTemplate.create()
                                    : ItemStack.EMPTY
                    )
                    || !canStoreAllChanges(before, candidate)) {
                failure = "insufficient_output_capacity";
                break;
            }
            after = candidate;
            executable++;
        }

        Preview transactionPreview = executable == 0
                ? Preview.rejected(
                        inventory.revision(),
                        before,
                        failure
                )
                : Preview.accepted(inventory.revision(), before, after);
        ItemStack consumed = executable == 0
                ? ItemStack.EMPTY
                : originalInput.copyWithCount(executable);
        ItemStack result = recipe.resultStack();
        ItemStack produced = executable == 0
                ? ItemStack.EMPTY
                : result.copyWithCount(result.getCount() * executable);
        List<ItemStack> remainders;
        if (remainderTemplate == null || executable == 0) {
            remainders = List.of();
        } else {
            ItemStack remainder = remainderTemplate.create();
            remainders = List.of(remainder.copyWithCount(
                    remainder.getCount() * executable
            ));
        }
        return new OperationPreview(
                requested,
                executable,
                consumed,
                produced,
                recipe.durabilityCost() * executable,
                remainders,
                inventory.revision(),
                failure,
                transactionPreview
        );
    }

    public boolean commit(Preview preview) {
        if (!preview.accepted() || preview.revision() != inventory.revision()) {
            return false;
        }
        if (!sameStacks(preview.expectedSlots(), snapshot())) {
            return false;
        }

        List<SlotDelta> deltas = preview.slotDeltas();
        for (SlotDelta delta : deltas) {
            if (!inventory.canStore(delta.slot(), delta.after())) {
                return false;
            }
        }
        List<SlotDelta> written = new ArrayList<>();
        try {
            for (SlotDelta delta : deltas) {
                written.add(delta);
                inventory.setItem(delta.slot(), delta.after());
            }
            return true;
        } catch (RuntimeException exception) {
            for (int index = written.size() - 1; index >= 0; index--) {
                SlotDelta delta = written.get(index);
                try {
                    inventory.setItem(delta.slot(), delta.before());
                } catch (RuntimeException ignored) {
                    // InventoryView implementations must make restoration available.
                }
            }
            return false;
        }
    }

    /**
     * Commits only when every requested operation was simulated successfully.
     */
    public boolean commit(OperationPreview preview) {
        return preview.exact() && commit(preview.transactionPreview());
    }

    private boolean insert(List<ItemStack> stacks, ItemStack incoming) {
        if (incoming.isEmpty()) {
            return true;
        }
        ItemStack remaining = incoming.copy();
        for (int slot : outputSlots) {
            ItemStack existing = stacks.get(slot);
            if (ItemStack.isSameItemSameComponents(existing, remaining)) {
                int accepted = acceptedAmount(slot, existing, remaining);
                if (accepted > 0) {
                    existing.grow(accepted);
                    remaining.shrink(accepted);
                }
            }
            if (remaining.isEmpty()) {
                return true;
            }
        }
        for (int slot : outputSlots) {
            if (stacks.get(slot).isEmpty()) {
                int accepted = acceptedAmount(slot, ItemStack.EMPTY, remaining);
                if (accepted == 0) {
                    continue;
                }
                ItemStack placed = remaining.copyWithCount(accepted);
                stacks.set(slot, placed);
                remaining.shrink(accepted);
            }
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private int acceptedAmount(int slot, ItemStack existing, ItemStack incoming) {
        int maximum = Math.min(
                incoming.getCount(),
                incoming.getMaxStackSize() - existing.getCount()
        );
        for (int amount = maximum; amount > 0; amount--) {
            ItemStack candidate = existing.isEmpty()
                    ? incoming.copyWithCount(amount)
                    : existing.copyWithCount(existing.getCount() + amount);
            if (inventory.canStore(slot, candidate)) {
                return amount;
            }
        }
        return 0;
    }

    private boolean canStoreAllChanges(List<ItemStack> before, List<ItemStack> after) {
        for (int slot = 0; slot < before.size(); slot++) {
            if (!sameStacks(List.of(before.get(slot)), List.of(after.get(slot)))
                    && !inventory.canStore(slot, after.get(slot))) {
                return false;
            }
        }
        return true;
    }

    private void validateSlots() {
        if (toolSlot == inputSlot || outputSlots.isEmpty()) {
            throw new IllegalArgumentException("Manual processing requires separate tool, input, and output slots");
        }
        if (outputSlots.stream().distinct().count() != outputSlots.size()) {
            throw new IllegalArgumentException("Manual processing output slots must be unique");
        }
        for (int slot : outputSlots) {
            if (slot == toolSlot || slot == inputSlot || slot < 0 || slot >= inventory.size()) {
                throw new IllegalArgumentException("Manual processing output slot is invalid");
            }
        }
        if (toolSlot < 0 || toolSlot >= inventory.size()
                || inputSlot < 0 || inputSlot >= inventory.size()) {
            throw new IllegalArgumentException("Manual processing tool or input slot is invalid");
        }
    }

    private List<ItemStack> snapshot() {
        List<ItemStack> snapshot = new ArrayList<>(inventory.size());
        for (int slot = 0; slot < inventory.size(); slot++) {
            snapshot.add(inventory.getItem(slot).copy());
        }
        return List.copyOf(snapshot);
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        return stacks.stream()
                .map(ItemStack::copy)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private static boolean sameStacks(List<ItemStack> left, List<ItemStack> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int slot = 0; slot < left.size(); slot++) {
            ItemStack expected = left.get(slot);
            ItemStack actual = right.get(slot);
            if (expected.getCount() != actual.getCount()
                    || !ItemStack.isSameItemSameComponents(expected, actual)) {
                return false;
            }
        }
        return true;
    }

    public record SlotDelta(int slot, ItemStack before, ItemStack after) {
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

    public static final class Preview {
        private final boolean accepted;
        private final long revision;
        private final String failure;
        private final List<ItemStack> expectedSlots;
        private final List<ItemStack> projectedSlots;
        private final List<SlotDelta> slotDeltas;

        private Preview(
                boolean accepted,
                long revision,
                String failure,
                List<ItemStack> expectedSlots,
                List<ItemStack> projectedSlots
        ) {
            this.accepted = accepted;
            this.revision = revision;
            this.failure = failure;
            this.expectedSlots = copyStacks(expectedSlots);
            this.projectedSlots = copyStacks(projectedSlots);
            List<SlotDelta> deltas = new ArrayList<>();
            for (int slot = 0; slot < expectedSlots.size(); slot++) {
                if (!sameStacks(List.of(expectedSlots.get(slot)), List.of(projectedSlots.get(slot)))) {
                    deltas.add(new SlotDelta(slot, expectedSlots.get(slot), projectedSlots.get(slot)));
                }
            }
            this.slotDeltas = List.copyOf(deltas);
        }

        private static Preview accepted(long revision, List<ItemStack> before, List<ItemStack> after) {
            return new Preview(true, revision, "", before, after);
        }

        private static Preview rejected(long revision, List<ItemStack> before, String failure) {
            return new Preview(false, revision, failure, before, before);
        }

        public boolean accepted() {
            return accepted;
        }

        public long revision() {
            return revision;
        }

        public String failure() {
            return failure;
        }

        public ItemStack stackAt(int slot) {
            return projectedSlots.get(slot).copy();
        }

        public List<SlotDelta> slotDeltas() {
            return List.copyOf(slotDeltas);
        }

        private List<ItemStack> expectedSlots() {
            return expectedSlots;
        }
    }
}
