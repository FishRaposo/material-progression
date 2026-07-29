package dev.fishraposo.materialprogression.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.fishraposo.materialprogression.testsupport.MinecraftTestBootstrap;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import java.util.List;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ItemTransactionTest {
    private static ManualProcessingRecipe processingRecipe;

    @BeforeAll
    static void bootStrapMinecraft() {
        MinecraftTestBootstrap.bootStrap();
        processingRecipe = new ManualProcessingRecipe(
                Ingredient.of(Items.WOODEN_SWORD),
                Ingredient.of(Items.HONEY_BOTTLE),
                new ItemStack(Items.STICK, 2),
                1,
                20
        );
    }

    @Test
    void simulationRejectsAFullOutputWithoutChangingAnySlot() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                new ItemStack(Items.STICK, 64)
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2)
        );

        ItemTransaction.Preview preview = transaction.simulate();

        assertFalse(preview.accepted());
        assertTrue(inventory.getItem(1).is(Items.HONEY_BOTTLE));
        assertEquals(64, inventory.getItem(2).getCount());
    }

    @Test
    void simulationRejectsInsufficientToolDurabilityWithoutConsumption() {
        ItemStack exhaustedTool = new ItemStack(Items.WOODEN_SWORD);
        exhaustedTool.setDamageValue(exhaustedTool.getMaxDamage() - 1);
        TestInventory inventory = new TestInventory(
                exhaustedTool,
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );

        ItemTransaction.Preview preview = transaction.simulate();

        assertFalse(preview.accepted());
        assertEquals(exhaustedTool.getDamageValue(), inventory.getItem(0).getDamageValue());
        assertEquals(1, inventory.getItem(1).getCount());
    }

    @Test
    void simulationPlacesCraftingRemaindersAfterTheResultInStableSlotOrder() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(3, 2)
        );

        ItemTransaction.Preview preview = transaction.simulate();

        assertTrue(preview.accepted());
        assertEquals(2, preview.stackAt(2).getCount());
        assertTrue(preview.stackAt(2).is(Items.STICK));
        assertTrue(preview.stackAt(3).is(Items.GLASS_BOTTLE));
    }

    @Test
    void commitRollsBackWhenTheInventoryRevisionChangedAfterSimulation() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );
        ItemTransaction.Preview preview = transaction.simulate();
        inventory.setItem(3, new ItemStack(Items.DIRT));

        assertFalse(transaction.commit(preview));
        assertTrue(inventory.getItem(1).is(Items.HONEY_BOTTLE));
        assertTrue(inventory.getItem(3).is(Items.DIRT));
    }

    @Test
    void commitsARealContainerOnlyAfterThePreviewIsAccepted() {
        SimpleContainer container = new SimpleContainer(4);
        container.setItem(0, new ItemStack(Items.WOODEN_SWORD));
        container.setItem(1, new ItemStack(Items.HONEY_BOTTLE));
        InventoryView view = InventoryView.of(container);
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                view, processingRecipe, 0, 1, List.of(2, 3)
        );

        assertTrue(transaction.commit(transaction.simulate()));
        assertTrue(container.getItem(1).isEmpty());
        assertTrue(container.getItem(2).is(Items.STICK));
        assertEquals(2, container.getItem(2).getCount());
        assertTrue(container.getItem(3).is(Items.GLASS_BOTTLE));
    }

    @Test
    void declaredMachineOutputBypassesOnlyThePlayerPlacementFilter() {
        SimpleContainer container = new SimpleContainer(4) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return slot < 2;
            }
        };
        container.setItem(0, new ItemStack(Items.WOODEN_SWORD));
        container.setItem(1, new ItemStack(Items.HONEY_BOTTLE));
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                InventoryView.of(container, slot -> slot >= 2),
                processingRecipe,
                0,
                1,
                List.of(2, 3)
        );

        ItemTransaction.Preview preview = transaction.simulate();

        assertTrue(preview.accepted());
        assertTrue(transaction.commit(preview));
        assertFalse(container.canPlaceItem(2, new ItemStack(Items.STICK)));
        assertEquals(1, container.getItem(0).getDamageValue());
        assertTrue(container.getItem(2).is(Items.STICK));
        assertEquals(2, container.getItem(2).getCount());
    }

    @Test
    void rejectsARestrictiveItemHandlerDuringPlanning() {
        ItemStackHandler handler = new ItemStackHandler(4) {
            @Override
            public int getSlotLimit(int slot) {
                return slot == 2 ? 1 : 64;
            }
        };
        handler.setStackInSlot(0, new ItemStack(Items.WOODEN_SWORD));
        handler.setStackInSlot(1, new ItemStack(Items.HONEY_BOTTLE));
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                InventoryView.of(handler), processingRecipe, 0, 1, List.of(2, 3)
        );

        ItemTransaction.Preview preview = transaction.simulate();

        assertFalse(preview.accepted());
        assertTrue(handler.getStackInSlot(1).is(Items.HONEY_BOTTLE));
        assertTrue(handler.getStackInSlot(2).isEmpty());
    }

    @Test
    void commitsThroughTheModifiableItemHandlerAdapter() {
        ItemStackHandler handler = new ItemStackHandler(4);
        handler.setStackInSlot(0, new ItemStack(Items.WOODEN_SWORD));
        handler.setStackInSlot(1, new ItemStack(Items.HONEY_BOTTLE));
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                InventoryView.of((IItemHandler) handler), processingRecipe, 0, 1, List.of(2, 3)
        );

        assertTrue(transaction.commit(transaction.simulate()));
        assertTrue(handler.getStackInSlot(1).isEmpty());
        assertTrue(handler.getStackInSlot(2).is(Items.STICK));
        assertTrue(handler.getStackInSlot(3).is(Items.GLASS_BOTTLE));
    }

    @Test
    void acceptsAnItemHandlerOnlyWhenItCanSetSlotsAtomically() {
        PartialInsertionHandler handler = new PartialInsertionHandler();

        assertThrows(IllegalArgumentException.class, () -> InventoryView.of((IItemHandler) handler));
        assertEquals(0, handler.insertCalls);
        assertTrue(handler.stack.isEmpty());
    }

    @Test
    void rollsBackASlotThatMutatesThenThrowsMidCommit() {
        ThrowingInventory inventory = new ThrowingInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );

        assertFalse(transaction.commit(transaction.simulate()));
        assertEquals(0, inventory.getItem(0).getDamageValue());
        assertTrue(inventory.getItem(1).is(Items.HONEY_BOTTLE));
        assertTrue(inventory.getItem(2).isEmpty());
        assertTrue(inventory.getItem(3).isEmpty());
    }

    @Test
    void rejectsAChangedSnapshotEvenWhenTheRevisionDidNotChange() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );
        ItemTransaction.Preview preview = transaction.simulate();
        inventory.replaceWithoutRevision(3, new ItemStack(Items.DIRT));

        assertFalse(transaction.commit(preview));
        assertTrue(inventory.getItem(1).is(Items.HONEY_BOTTLE));
        assertTrue(inventory.getItem(3).is(Items.DIRT));
    }

    @Test
    void previewDoesNotExposeMutablePlannedStacks() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction.Preview preview = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        ).simulate();

        ItemStack exposed = preview.stackAt(2);
        exposed.setCount(1);

        assertEquals(2, preview.stackAt(2).getCount());
        assertThrows(UnsupportedOperationException.class, () -> preview.slotDeltas().clear());
    }

    @Test
    void batchPreviewReportsTheMaximumWithoutCommittingAPartialRequest() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE, 5),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );

        OperationPreview limited = transaction.simulateBatch(10);

        assertEquals(10, limited.requested());
        assertEquals(5, limited.executable());
        assertEquals(5, limited.consumed().getCount());
        assertEquals(10, limited.produced().getCount());
        assertEquals(5, limited.durabilityCost());
        assertEquals(5, limited.remainders().getFirst().getCount());
        assertFalse(transaction.commit(limited));
        assertEquals(5, inventory.getItem(1).getCount());
        assertTrue(inventory.getItem(2).isEmpty());

        OperationPreview exact = transaction.simulateBatch(5);

        assertTrue(transaction.commit(exact));
        assertTrue(inventory.getItem(1).isEmpty());
        assertEquals(5, inventory.getItem(0).getDamageValue());
        assertEquals(10, inventory.getItem(2).getCount());
        assertEquals(5, inventory.getItem(3).getCount());
    }

    @Test
    void staleBatchPreviewConsumesNothing() {
        TestInventory inventory = new TestInventory(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.HONEY_BOTTLE, 2),
                ItemStack.EMPTY,
                ItemStack.EMPTY
        );
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                inventory, processingRecipe, 0, 1, List.of(2, 3)
        );
        OperationPreview preview = transaction.simulateBatch(2);
        inventory.setItem(2, new ItemStack(Items.DIRT));

        assertFalse(transaction.commit(preview));
        assertEquals(2, inventory.getItem(1).getCount());
        assertEquals(0, inventory.getItem(0).getDamageValue());
        assertTrue(inventory.getItem(2).is(Items.DIRT));
    }

    private static class TestInventory implements InventoryView {
        private final ItemStack[] items;
        private long revision;

        private TestInventory(ItemStack... items) {
            this.items = items;
        }

        @Override
        public ItemStack getItem(int slot) {
            return items[slot];
        }

        @Override
        public int size() {
            return items.length;
        }

        @Override
        public long revision() {
            return revision;
        }

        @Override
        public boolean canStore(int slot, ItemStack stack) {
            return true;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            items[slot] = stack;
            revision++;
        }

        private void replaceWithoutRevision(int slot, ItemStack stack) {
            items[slot] = stack;
        }
    }

    private static final class ThrowingInventory extends TestInventory {
        private boolean throwOnce = true;

        private ThrowingInventory(ItemStack... items) {
            super(items);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            super.setItem(slot, stack);
            if (slot == 1 && throwOnce) {
                throwOnce = false;
                throw new IllegalStateException("Injected write failure");
            }
        }
    }

    private static final class PartialInsertionHandler implements IItemHandler {
        private ItemStack stack = ItemStack.EMPTY;
        private int insertCalls;

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return stack;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack incoming, boolean simulate) {
            insertCalls++;
            if (!simulate) {
                stack = incoming.copyWithCount(1);
            }
            return incoming.copyWithCount(Math.max(0, incoming.getCount() - 1));
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    }
}
