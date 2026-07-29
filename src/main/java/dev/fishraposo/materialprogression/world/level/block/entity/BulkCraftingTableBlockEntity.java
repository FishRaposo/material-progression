package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import dev.fishraposo.materialprogression.world.item.BulkCraftingUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class BulkCraftingTableBlockEntity
        extends BaseContainerBlockEntity
        implements WorldlyContainer {
    public static final int DATA_VERSION = 1;
    public static final int BUFFER_SLOT_COUNT = 18;
    public static final int BASE_ACTIVE_BUFFER_SLOTS = 9;
    public static final int UPGRADE_SLOT_START = BUFFER_SLOT_COUNT;
    public static final int UPGRADE_SLOT_COUNT = 5;
    public static final int SLOT_COUNT =
            BUFFER_SLOT_COUNT + UPGRADE_SLOT_COUNT;
    private static final Component DEFAULT_NAME = Component.translatable(
            "container.material_progression.bulk_crafting_table"
    );

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private @Nullable String selectedRecipe;

    public BulkCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BULK_CRAFTING_TABLE.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory
    ) {
        return new BulkCraftingTableMenu(containerId, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return false;
        }
        if (slot < BUFFER_SLOT_COUNT) {
            return slot < activeBufferSlots()
                    && !(stack.getItem()
                            instanceof BulkCraftingUpgradeItem);
        }
        if (!(stack.getItem() instanceof BulkCraftingUpgradeItem upgrade)) {
            return false;
        }
        return upgrade.stackableCapability()
                || !hasFamily(upgrade.family(), slot);
    }

    public int activeBufferSlots() {
        return Math.min(
                BUFFER_SLOT_COUNT,
                BASE_ACTIVE_BUFFER_SLOTS
                        + moduleCapacity(
                                BulkCraftingUpgradeItem.Family.STORAGE
                        ) * 3
        );
    }

    public int moduleCapacity(BulkCraftingUpgradeItem.Family family) {
        int capacity = 0;
        for (int slot = UPGRADE_SLOT_START; slot < SLOT_COUNT; slot++) {
            ItemStack stack = getItem(slot);
            if (stack.getItem() instanceof BulkCraftingUpgradeItem upgrade
                    && upgrade.family() == family) {
                if (upgrade.stackableCapability()) {
                    capacity += upgrade.units();
                } else {
                    capacity = Math.max(capacity, upgrade.units());
                }
            }
        }
        return capacity;
    }

    public boolean installUpgrade(ItemStack offered) {
        if (!(offered.getItem()
                instanceof BulkCraftingUpgradeItem incoming)
                || offered.isEmpty()) {
            return false;
        }
        for (int slot = UPGRADE_SLOT_START; slot < SLOT_COUNT; slot++) {
            ItemStack installed = getItem(slot);
            if (installed.getItem()
                    instanceof BulkCraftingUpgradeItem existing
                    && existing.family() == incoming.family()
                    && incoming.tier() > existing.tier()) {
                setItem(slot, offered.copyWithCount(1));
                offered.shrink(1);
                return true;
            }
        }
        if (!incoming.stackableCapability()
                && hasFamily(incoming.family(), -1)) {
            return false;
        }
        for (int slot = UPGRADE_SLOT_START; slot < SLOT_COUNT; slot++) {
            if (getItem(slot).isEmpty()) {
                setItem(slot, offered.copyWithCount(1));
                offered.shrink(1);
                return true;
            }
        }
        return false;
    }

    public @Nullable String selectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(@Nullable String selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return java.util.stream.IntStream.range(0, activeBufferSlots())
                .toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return slot >= 0 && slot < BUFFER_SLOT_COUNT;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        String selected = input.getStringOr("SelectedRecipe", "");
        selectedRecipe = selected.isEmpty() ? null : selected;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("DataVersion", DATA_VERSION);
        ContainerHelper.saveAllItems(output, items);
        if (selectedRecipe != null) {
            output.putString("SelectedRecipe", selectedRecipe);
        }
    }

    private boolean hasFamily(
            BulkCraftingUpgradeItem.Family family,
            int ignoredSlot
    ) {
        for (int slot = UPGRADE_SLOT_START; slot < SLOT_COUNT; slot++) {
            if (slot == ignoredSlot) {
                continue;
            }
            if (getItem(slot).getItem()
                    instanceof BulkCraftingUpgradeItem upgrade
                    && upgrade.family() == family) {
                return true;
            }
        }
        return false;
    }
}
