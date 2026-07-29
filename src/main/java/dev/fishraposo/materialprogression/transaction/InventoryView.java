package dev.fishraposo.materialprogression.transaction;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * A revisioned slot view used by transactional gameplay operations.
 *
 * <p>Callers that own inventory persistence should expose their own increasing
 * revision. The stock adapters additionally retain a stack snapshot check at
 * commit time, so a direct external mutation cannot silently commit a stale
 * transaction.</p>
 */
public interface InventoryView {
    ItemStack getItem(int slot);

    int size();

    long revision();

    /**
     * Dry-runs replacing one slot with this complete stack without mutating it.
     * Implementations must include their item filter and per-slot capacity.
     */
    boolean canStore(int slot, ItemStack stack);

    /**
     * Replaces one slot with an already accepted stack. This operation must be
     * usable again with the previous stack if a later transaction write fails.
     */
    void setItem(int slot, ItemStack stack);

    static InventoryView of(Container container) {
        return new ContainerView(container);
    }

    static InventoryView of(IItemHandler handler) {
        if (!(handler instanceof IItemHandlerModifiable modifiable)) {
            throw new IllegalArgumentException(
                    "Atomic transactions require an IItemHandlerModifiable"
            );
        }
        return of(modifiable);
    }

    static InventoryView of(IItemHandlerModifiable handler) {
        return new ItemHandlerView(handler);
    }

    final class ContainerView implements InventoryView {
        private final Container container;
        private long revision;

        private ContainerView(Container container) {
            this.container = container;
        }

        @Override
        public ItemStack getItem(int slot) {
            return container.getItem(slot);
        }

        @Override
        public int size() {
            return container.getContainerSize();
        }

        @Override
        public long revision() {
            return revision;
        }

        @Override
        public boolean canStore(int slot, ItemStack stack) {
            return stack.isEmpty() || (container.canPlaceItem(slot, stack)
                    && stack.getCount() <= Math.min(
                            container.getMaxStackSize(),
                            stack.getMaxStackSize()
                    ));
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            container.setItem(slot, stack);
            revision++;
        }
    }

    final class ItemHandlerView implements InventoryView {
        private final IItemHandlerModifiable handler;
        private long revision;

        private ItemHandlerView(IItemHandlerModifiable handler) {
            this.handler = handler;
        }

        @Override
        public ItemStack getItem(int slot) {
            return handler.getStackInSlot(slot);
        }

        @Override
        public int size() {
            return handler.getSlots();
        }

        @Override
        public long revision() {
            return revision;
        }

        @Override
        public boolean canStore(int slot, ItemStack stack) {
            return stack.isEmpty() || (handler.isItemValid(slot, stack)
                    && stack.getCount() <= Math.min(
                            handler.getSlotLimit(slot),
                            stack.getMaxStackSize()
                    ));
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!canStore(slot, stack)) {
                throw new IllegalStateException("Item handler rejected a simulated transaction slot");
            }
            handler.setStackInSlot(slot, stack.copy());
            revision++;
        }
    }
}
