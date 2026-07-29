package dev.fishraposo.materialprogression.world.inventory;

import dev.fishraposo.materialprogression.network.BulkCraftingPreviewPayload;
import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.world.item.BulkCraftingUpgradeItem;
import dev.fishraposo.materialprogression.world.level.block.entity.BulkCraftingTableBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class BulkCraftingTableMenu extends AbstractContainerMenu {
    private static final int PLAYER_SLOT_START =
            BulkCraftingTableBlockEntity.SLOT_COUNT;
    private final Container container;
    private final Inventory playerInventory;
    private long previewSequence;

    public BulkCraftingTableMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new SimpleContainer(BulkCraftingTableBlockEntity.SLOT_COUNT)
        );
    }

    public BulkCraftingTableMenu(
            int containerId,
            Inventory inventory,
            Container container
    ) {
        super(ModMenus.BULK_CRAFTING_TABLE.get(), containerId);
        checkContainerSize(
                container,
                BulkCraftingTableBlockEntity.SLOT_COUNT
        );
        this.container = container;
        this.playerInventory = inventory;
        container.startOpen(inventory.player);

        for (int slot = 0;
                slot < BulkCraftingTableBlockEntity.BUFFER_SLOT_COUNT;
                slot++) {
            int column = slot % 9;
            int row = slot / 9;
            addSlot(new Slot(
                    container,
                    slot,
                    8 + column * 18,
                    18 + row * 18
            ));
        }
        for (int slot = 0;
                slot < BulkCraftingTableBlockEntity.UPGRADE_SLOT_COUNT;
                slot++) {
            int containerSlot =
                    BulkCraftingTableBlockEntity.UPGRADE_SLOT_START + slot;
            addSlot(new Slot(
                    container,
                    containerSlot,
                    44 + slot * 18,
                    58
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem()
                            instanceof BulkCraftingUpgradeItem
                            && container.canPlaceItem(
                                    containerSlot,
                                    stack
                            );
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                ));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    142
            ));
        }
    }

    public void sendPreview(
            Player player,
            String query,
            String target,
            int requested
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)
                || !(container
                        instanceof BulkCraftingTableBlockEntity table)) {
            return;
        }
        previewSequence++;
        var targets = table.searchableTargets(query)
                .stream()
                .limit(256)
                .toList();
        String selected = target;
        if (selected == null || selected.isBlank()
                || !targets.contains(selected)) {
            selected = targets.isEmpty() ? "" : targets.getFirst();
        }
        var preview = selected.isEmpty()
                ? null
                : table.preview(playerInventory, selected, requested);
        PacketDistributor.sendToPlayer(
                serverPlayer,
                preview == null
                        ? BulkCraftingPreviewPayload.rejected(
                                containerId,
                                previewSequence,
                                targets,
                                selected,
                                requested,
                                selected.isEmpty()
                                        ? "no_recipe"
                                        : "invalid_request"
                        )
                        : BulkCraftingPreviewPayload.from(
                                containerId,
                                previewSequence,
                                targets,
                                preview
                        )
        );
    }

    public boolean execute(
            Player player,
            String target,
            int requested,
            long fingerprint,
            long sequence
    ) {
        if (sequence != previewSequence
                || !(container
                        instanceof BulkCraftingTableBlockEntity table)) {
            return false;
        }
        boolean committed = table.execute(
                playerInventory,
                target,
                requested,
                fingerprint
        );
        sendPreview(player, "", target, requested);
        return committed;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (!source.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = source.getItem();
        ItemStack copy = stack.copy();
        if (index < PLAYER_SLOT_START) {
            if (!moveItemStackTo(
                    stack,
                    PLAYER_SLOT_START,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof BulkCraftingUpgradeItem) {
            if (!moveItemStackTo(
                    stack,
                    BulkCraftingTableBlockEntity.UPGRADE_SLOT_START,
                    PLAYER_SLOT_START,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(
                stack,
                0,
                BulkCraftingTableBlockEntity.BUFFER_SLOT_COUNT,
                false
        )) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            source.setByPlayer(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }
        source.onTake(player, stack);
        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
