package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.transaction.InventoryView;
import dev.fishraposo.materialprogression.transaction.ItemTransaction;
import dev.fishraposo.materialprogression.transaction.OperationPreview;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class WorkshopBlockEntity extends BaseContainerBlockEntity {
    public static final int DATA_VERSION = 1;
    public static final int TOOL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int SLOT_COUNT = 3;
    private static final Component DEFAULT_NAME =
            Component.translatable("container.material_progression.workshop");

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private @Nullable Identifier selectedRecipeId;
    private long inventoryRevision;
    private final InventoryView transactionInventory = new InventoryView() {
        @Override
        public ItemStack getItem(int slot) {
            return WorkshopBlockEntity.this.getItem(slot);
        }

        @Override
        public int size() {
            return SLOT_COUNT;
        }

        @Override
        public long revision() {
            return inventoryRevision;
        }

        @Override
        public boolean canStore(int slot, ItemStack stack) {
            return stack.isEmpty() || ((slot == OUTPUT_SLOT
                    || WorkshopBlockEntity.this.canPlaceItem(slot, stack))
                    && stack.getCount() <= Math.min(
                            WorkshopBlockEntity.this.getMaxStackSize(),
                            stack.getMaxStackSize()
                    ));
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            WorkshopBlockEntity.this.setItem(slot, stack);
        }
    };

    public WorkshopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WORKSHOP.get(), pos, state);
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
        return new WorkshopMenu(containerId, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == OUTPUT_SLOT) {
            return false;
        }
        return slot != TOOL_SLOT || isWorkshopTool(stack);
    }

    public List<RecipeHolder<ManualProcessingRecipe>> matchingRecipes() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        ItemStack tool = getItem(TOOL_SLOT);
        ItemStack input = getItem(INPUT_SLOT);
        return serverLevel.recipeAccess()
                .recipeMap()
                .byType(ModRecipes.MANUAL_PROCESSING.get())
                .stream()
                .filter(holder -> holder.value().matches(tool, input))
                .sorted(Comparator.comparing(
                        holder -> holder.id().identifier().toString()
                ))
                .toList();
    }

    public boolean selectRecipe(Identifier recipeId) {
        boolean valid = matchingRecipes().stream().anyMatch(
                holder -> holder.id().identifier().equals(recipeId)
        );
        if (!valid) {
            return false;
        }
        selectedRecipeId = recipeId;
        setChanged();
        return true;
    }

    public boolean executeSelected() {
        if (selectedRecipeId == null) {
            return false;
        }
        OperationPreview preview = preview(
                selectedRecipeId,
                1
        );
        return preview != null
                && execute(
                        selectedRecipeId,
                        1,
                        preview.revision()
                );
    }

    public @Nullable OperationPreview preview(
            Identifier recipeId,
            int requested
    ) {
        ManualProcessingRecipe recipe = resolveRecipe(recipeId);
        if (recipe == null) {
            return null;
        }
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                transactionInventory,
                recipe,
                TOOL_SLOT,
                INPUT_SLOT,
                List.of(OUTPUT_SLOT)
        );
        return transaction.simulateBatch(requested);
    }

    public boolean execute(
            Identifier recipeId,
            int requested,
            long expectedRevision
    ) {
        ManualProcessingRecipe recipe = resolveRecipe(recipeId);
        if (recipe == null || inventoryRevision != expectedRevision) {
            return false;
        }
        ItemTransaction transaction = ItemTransaction.manualProcessing(
                transactionInventory,
                recipe,
                TOOL_SLOT,
                INPUT_SLOT,
                List.of(OUTPUT_SLOT)
        );
        OperationPreview preview = transaction.simulateBatch(requested);
        if (preview.revision() != expectedRevision) {
            return false;
        }
        boolean committed = transaction.commit(preview);
        if (committed) {
            setChanged();
        }
        return committed;
    }

    public @Nullable Identifier selectedRecipeId() {
        return selectedRecipeId;
    }

    public long inventoryRevision() {
        return inventoryRevision;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        inventoryRevision++;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = super.removeItem(slot, amount);
        if (!removed.isEmpty()) {
            inventoryRevision++;
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        if (!removed.isEmpty()) {
            inventoryRevision++;
        }
        return removed;
    }

    @Override
    public void clearContent() {
        boolean hadItems = items.stream().anyMatch(stack -> !stack.isEmpty());
        super.clearContent();
        if (hadItems) {
            inventoryRevision++;
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        inventoryRevision = 0;
        String selected = input.getStringOr("SelectedRecipe", "");
        selectedRecipeId = selected.isEmpty() ? null : Identifier.tryParse(selected);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("DataVersion", DATA_VERSION);
        ContainerHelper.saveAllItems(output, items);
        if (selectedRecipeId != null) {
            output.putString("SelectedRecipe", selectedRecipeId.toString());
        }
    }

    private static boolean isWorkshopTool(ItemStack stack) {
        return stack.is(ModTags.KNIVES)
                || stack.is(ModTags.HAMMERS)
                || stack.is(ModTags.SAWS);
    }

    private @Nullable ManualProcessingRecipe resolveRecipe(
            Identifier recipeId
    ) {
        return matchingRecipes().stream()
                .filter(holder ->
                        holder.id().identifier().equals(recipeId))
                .map(RecipeHolder::value)
                .findFirst()
                .orElse(null);
    }
}
