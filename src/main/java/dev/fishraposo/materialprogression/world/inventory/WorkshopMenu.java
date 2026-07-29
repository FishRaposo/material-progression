package dev.fishraposo.materialprogression.world.inventory;

import dev.fishraposo.materialprogression.registry.ModMenus;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import dev.fishraposo.materialprogression.world.level.block.entity.WorkshopBlockEntity;
import java.util.Comparator;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public final class WorkshopMenu extends AbstractContainerMenu {
    private static final int PLAYER_SLOT_START =
            WorkshopBlockEntity.SLOT_COUNT;
    private final Container container;
    private final Level level;

    public WorkshopMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(
                WorkshopBlockEntity.SLOT_COUNT
        ));
    }

    public WorkshopMenu(
            int containerId,
            Inventory inventory,
            Container container
    ) {
        super(ModMenus.WORKSHOP.get(), containerId);
        checkContainerSize(container, WorkshopBlockEntity.SLOT_COUNT);
        this.container = container;
        this.level = inventory.player.level();
        container.startOpen(inventory.player);

        addSlot(new Slot(container, WorkshopBlockEntity.TOOL_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModTags.KNIVES)
                        || stack.is(ModTags.HAMMERS)
                        || stack.is(ModTags.SAWS);
            }
        });
        addSlot(new Slot(container, WorkshopBlockEntity.INPUT_SLOT, 80, 35));
        addSlot(new Slot(container, WorkshopBlockEntity.OUTPUT_SLOT, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    public List<RecipeHolder<ManualProcessingRecipe>> matchingRecipes() {
        ItemStack tool = container.getItem(WorkshopBlockEntity.TOOL_SLOT);
        ItemStack input = container.getItem(WorkshopBlockEntity.INPUT_SLOT);
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
        return container instanceof WorkshopBlockEntity workshop
                && workshop.selectRecipe(recipeId);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return id == 0
                && container instanceof WorkshopBlockEntity workshop
                && workshop.executeSelected();
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
            if (!moveItemStackTo(stack, PLAYER_SLOT_START, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (isWorkshopTool(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 1, 2, false)) {
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

    private static boolean isWorkshopTool(ItemStack stack) {
        return stack.is(ModTags.KNIVES)
                || stack.is(ModTags.HAMMERS)
                || stack.is(ModTags.SAWS);
    }
}
