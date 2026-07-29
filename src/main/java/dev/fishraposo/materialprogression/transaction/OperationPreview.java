package dev.fishraposo.materialprogression.transaction;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** Immutable summary of a simulated manual-processing batch. */
public final class OperationPreview {
    private final int requested;
    private final int executable;
    private final ItemStack consumed;
    private final ItemStack produced;
    private final int durabilityCost;
    private final List<ItemStack> remainders;
    private final long revision;
    private final String failure;
    private final ItemTransaction.Preview transactionPreview;

    OperationPreview(
            int requested,
            int executable,
            ItemStack consumed,
            ItemStack produced,
            int durabilityCost,
            List<ItemStack> remainders,
            long revision,
            String failure,
            ItemTransaction.Preview transactionPreview
    ) {
        this.requested = requested;
        this.executable = executable;
        this.consumed = consumed.copy();
        this.produced = produced.copy();
        this.durabilityCost = durabilityCost;
        this.remainders = remainders.stream()
                .map(ItemStack::copy)
                .toList();
        this.revision = revision;
        this.failure = failure;
        this.transactionPreview = transactionPreview;
    }

    public int requested() {
        return requested;
    }

    public int executable() {
        return executable;
    }

    public ItemStack consumed() {
        return consumed.copy();
    }

    public ItemStack produced() {
        return produced.copy();
    }

    public int durabilityCost() {
        return durabilityCost;
    }

    public List<ItemStack> remainders() {
        return remainders.stream().map(ItemStack::copy).toList();
    }

    public long revision() {
        return revision;
    }

    public String failure() {
        return failure;
    }

    public boolean exact() {
        return executable > 0 && executable == requested;
    }

    ItemTransaction.Preview transactionPreview() {
        return transactionPreview;
    }
}
