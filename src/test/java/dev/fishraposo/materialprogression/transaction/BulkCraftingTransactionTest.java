package dev.fishraposo.materialprogression.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.fishraposo.materialprogression.planner.CraftingPlan;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class BulkCraftingTransactionTest {
    @Test
    void consumesSourcesInOrderAndPlacesTargetAndSurplusInBuffer() {
        SimpleContainer buffer = new SimpleContainer(4);
        SimpleContainer player = new SimpleContainer(2);
        buffer.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        player.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        InventoryView bufferView = InventoryView.of(buffer, slot -> true);
        InventoryView playerView = InventoryView.of(player);
        BulkCraftingTransaction transaction = new BulkCraftingTransaction(
                List.of(
                        slice(bufferView, 0, 1),
                        slice(playerView, 0, 1)
                ),
                slice(bufferView, 0, 1, 2, 3)
        );

        CraftingPlan plan = successful(
                Map.of("minecraft:oak_log", 2),
                Map.of("minecraft:oak_planks", 2)
        );
        BulkCraftingTransaction.Preview preview =
                transaction.simulate(plan, "minecraft:stick", 8);

        assertTrue(preview.accepted());
        assertTrue(transaction.commit(preview));
        assertTrue(buffer.getItem(0).is(Items.OAK_PLANKS));
        assertEquals(2, buffer.getItem(0).getCount());
        assertTrue(buffer.getItem(1).is(Items.STICK));
        assertEquals(8, buffer.getItem(1).getCount());
        assertTrue(player.getItem(0).isEmpty());
    }

    @Test
    void stalePreviewConsumesNothing() {
        SimpleContainer buffer = new SimpleContainer(2);
        buffer.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        InventoryView view = InventoryView.of(buffer, slot -> true);
        BulkCraftingTransaction transaction = new BulkCraftingTransaction(
                List.of(slice(view, 0, 1)),
                slice(view, 0, 1)
        );
        BulkCraftingTransaction.Preview preview = transaction.simulate(
                successful(Map.of("minecraft:oak_log", 1), Map.of()),
                "minecraft:oak_planks",
                4
        );

        buffer.setItem(0, new ItemStack(Items.BIRCH_LOG, 1));

        assertFalse(transaction.commit(preview));
        assertTrue(buffer.getItem(0).is(Items.BIRCH_LOG));
        assertTrue(buffer.getItem(1).isEmpty());
    }

    @Test
    void fullOutputRejectsWithoutMutation() {
        SimpleContainer input = new SimpleContainer(1);
        SimpleContainer output = new SimpleContainer(1);
        input.setItem(0, new ItemStack(Items.OAK_LOG, 1));
        output.setItem(0, new ItemStack(Items.COBBLESTONE, 64));
        InventoryView inputView = InventoryView.of(input);
        InventoryView outputView = InventoryView.of(output, slot -> true);
        BulkCraftingTransaction transaction = new BulkCraftingTransaction(
                List.of(slice(inputView, 0)),
                slice(outputView, 0)
        );

        BulkCraftingTransaction.Preview preview = transaction.simulate(
                successful(Map.of("minecraft:oak_log", 1), Map.of()),
                "minecraft:oak_planks",
                4
        );

        assertFalse(preview.accepted());
        assertEquals("insufficient_output_capacity", preview.failure());
        assertTrue(input.getItem(0).is(Items.OAK_LOG));
        assertTrue(output.getItem(0).is(Items.COBBLESTONE));
    }

    private static BulkCraftingTransaction.Slice slice(
            InventoryView view,
            Integer... slots
    ) {
        return new BulkCraftingTransaction.Slice(
                view,
                List.of(slots)
        );
    }

    private static CraftingPlan successful(
            Map<String, Integer> consumed,
            Map<String, Integer> surplus
    ) {
        return new CraftingPlan(
                List.of(),
                consumed,
                surplus,
                Optional.empty()
        );
    }
}
