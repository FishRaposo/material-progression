package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.transaction.InventoryView;
import dev.fishraposo.materialprogression.transaction.ItemTransaction;
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
        if (level == null) {
            return List.of();
        }
        ItemStack tool = getItem(TOOL_SLOT);
        ItemStack input = getItem(INPUT_SLOT);
        return level.recipeAccess()
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
        ManualProcessingRecipe recipe = matchingRecipes().stream()
                .filter(holder ->
                        holder.id().identifier().equals(selectedRecipeId))
                .map(RecipeHolder::value)
                .findFirst()
                .orElse(null);
        if (recipe == null) {
            return false;
        }

        ItemTransaction transaction = ItemTransaction.manualProcessing(
                InventoryView.of(this),
                recipe,
                TOOL_SLOT,
                INPUT_SLOT,
                List.of(OUTPUT_SLOT)
        );
        ItemTransaction.Preview preview = transaction.simulate();
        boolean committed = transaction.commit(preview);
        if (committed) {
            setChanged();
        }
        return committed;
    }

    public @Nullable Identifier selectedRecipeId() {
        return selectedRecipeId;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
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
}
