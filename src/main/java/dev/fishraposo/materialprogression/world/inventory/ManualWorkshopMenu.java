package dev.fishraposo.materialprogression.world.inventory;

import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.world.level.block.entity.ManualWorkshopBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ManualWorkshopMenu extends AbstractContainerMenu {
    private static final int WORKSHOP_SLOT_COUNT = 3;
    private static final int PLAYER_SLOT_START = WORKSHOP_SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;
    private final Container workshop;
    private final ContainerData data;

    public ManualWorkshopMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new SimpleContainer(WORKSHOP_SLOT_COUNT),
                new SimpleContainerData(
                        ManualWorkshopBlockEntity.DATA_COUNT
                )
        );
    }

    public ManualWorkshopMenu(
            int containerId,
            Inventory inventory,
            Container workshop,
            ContainerData data
    ) {
        super(ModMenus.MANUAL_WORKSHOP.get(), containerId);
        checkContainerSize(workshop, WORKSHOP_SLOT_COUNT);
        checkContainerDataCount(
                data,
                ManualWorkshopBlockEntity.DATA_COUNT
        );
        this.workshop = workshop;
        this.data = data;

        addSlot(new Slot(
                workshop,
                ManualWorkshopBlockEntity.TOOL_SLOT,
                56,
                53
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ManualWorkshopBlockEntity.isWorkshopTool(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(
                workshop,
                ManualWorkshopBlockEntity.INPUT_SLOT,
                56,
                17
        ));
        addSlot(new Slot(
                workshop,
                ManualWorkshopBlockEntity.OUTPUT_SLOT,
                116,
                35
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(data);
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
                    PLAYER_SLOT_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else if (ManualWorkshopBlockEntity.isWorkshopTool(stack)) {
            if (!moveItemStackTo(
                    stack,
                    ManualWorkshopBlockEntity.TOOL_SLOT,
                    ManualWorkshopBlockEntity.TOOL_SLOT + 1,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(
                stack,
                ManualWorkshopBlockEntity.INPUT_SLOT,
                ManualWorkshopBlockEntity.INPUT_SLOT + 1,
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
    public boolean stillValid(Player player) {
        return workshop.stillValid(player);
    }

    public int progress() {
        return data.get(ManualWorkshopBlockEntity.DATA_PROGRESS);
    }

    public int maxProgress() {
        return data.get(ManualWorkshopBlockEntity.DATA_MAX_PROGRESS);
    }

    public int progressWidth(int width) {
        int maximum = maxProgress();
        return maximum <= 0
                ? 0
                : Math.min(width, progress() * width / maximum);
    }
}
