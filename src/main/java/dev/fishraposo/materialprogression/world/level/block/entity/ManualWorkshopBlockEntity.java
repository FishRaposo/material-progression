package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.world.inventory.ManualWorkshopMenu;
import dev.fishraposo.materialprogression.world.item.crafting.ManualWorkshopRecipe;
import dev.fishraposo.materialprogression.world.item.crafting.ManualWorkshopRecipeInput;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class ManualWorkshopBlockEntity
        extends BaseContainerBlockEntity
        implements WorldlyContainer {
    public static final int TOOL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;
    public static final int DATA_COUNT = 2;
    private static final int[] NO_SLOTS = new int[0];
    private static final Component DEFAULT_NAME = Component.translatable(
            "container.material_progression.manual_workshop"
    );

    private final RecipeManager.CachedCheck<
            ManualWorkshopRecipeInput,
            ManualWorkshopRecipe
    > recipeCheck = RecipeManager.createCheck(
            ModRecipes.MANUAL_WORKSHOP.get()
    );
    private NonNullList<ItemStack> items = NonNullList.withSize(
            SLOT_COUNT,
            ItemStack.EMPTY
    );
    private int progress;
    private int maxProgress;
    private @Nullable ResourceKey<Recipe<?>> activeRecipe;
    private ItemStack activeToolIdentity = ItemStack.EMPTY;
    private ItemStack activeInputIdentity = ItemStack.EMPTY;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> progress;
                case DATA_MAX_PROGRESS -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> progress = value;
                case DATA_MAX_PROGRESS -> maxProgress = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public ManualWorkshopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MANUAL_WORKSHOP.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            ManualWorkshopBlockEntity workshop
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack tool = workshop.items.get(TOOL_SLOT);
        ItemStack ingredient = workshop.items.get(INPUT_SLOT);
        ManualWorkshopRecipeInput input =
                new ManualWorkshopRecipeInput(tool, ingredient);
        Optional<RecipeHolder<ManualWorkshopRecipe>> match =
                workshop.recipeCheck.getRecipeFor(input, serverLevel);

        if (match.isEmpty()) {
            workshop.resetProgress();
            return;
        }

        RecipeHolder<ManualWorkshopRecipe> holder = match.get();
        ManualWorkshopRecipe recipe = holder.value();
        if (workshop.identityChanged(holder, tool, ingredient)) {
            workshop.progress = 0;
        }
        workshop.activeRecipe = holder.id();
        workshop.activeToolIdentity = identityOf(tool);
        workshop.activeInputIdentity = identityOf(ingredient);
        workshop.maxProgress = recipe.processingTime();

        ItemStack result = recipe.assemble(input);
        if (!workshop.canAcceptResult(result)) {
            workshop.setChanged();
            return;
        }

        workshop.progress++;
        if (workshop.progress % 20 == 0
                && workshop.progress < workshop.maxProgress) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.5,
                    1,
                    0.08,
                    0.02,
                    0.08,
                    0.0
            );
        }

        if (workshop.progress >= workshop.maxProgress) {
            workshop.complete(serverLevel, pos, recipe, result);
        } else {
            workshop.setChanged();
        }
    }

    private boolean identityChanged(
            RecipeHolder<ManualWorkshopRecipe> holder,
            ItemStack tool,
            ItemStack ingredient
    ) {
        if (activeRecipe == null) {
            return progress > 0;
        }
        return !activeRecipe.equals(holder.id())
                || !sameIdentity(activeToolIdentity, tool)
                || !sameIdentity(activeInputIdentity, ingredient);
    }

    private void complete(
            ServerLevel level,
            BlockPos pos,
            ManualWorkshopRecipe recipe,
            ItemStack result
    ) {
        ItemStack ingredient = items.get(INPUT_SLOT);
        ItemStack tool = items.get(TOOL_SLOT);
        if (ingredient.isEmpty()
                || tool.isEmpty()
                || !canAcceptResult(result)) {
            return;
        }

        ingredient.shrink(1);
        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            items.set(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }
        tool.hurtAndBreak(
                recipe.toolDamage(),
                level,
                (LivingEntity) null,
                ignored -> {
                }
        );

        progress = 0;
        activeRecipe = null;
        activeToolIdentity = ItemStack.EMPTY;
        activeInputIdentity = ItemStack.EMPTY;
        setChanged();
        syncTool();
        level.playSound(
                null,
                pos,
                SoundEvents.SMITHING_TABLE_USE,
                SoundSource.BLOCKS,
                0.65F,
                1.1F
        );
        level.sendParticles(
                ParticleTypes.CRIT,
                pos.getX() + 0.5,
                pos.getY() + 1.05,
                pos.getZ() + 0.5,
                4,
                0.18,
                0.05,
                0.18,
                0.02
        );
    }

    private boolean canAcceptResult(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return result.getCount() <= getMaxStackSize(result);
        }
        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount()
                <= Math.min(
                        output.getMaxStackSize(),
                        getMaxStackSize(output)
                );
    }

    private void resetProgress() {
        if (progress == 0 && maxProgress == 0 && activeRecipe == null) {
            return;
        }
        progress = 0;
        maxProgress = 0;
        activeRecipe = null;
        activeToolIdentity = ItemStack.EMPTY;
        activeInputIdentity = ItemStack.EMPTY;
        setChanged();
    }

    private static ItemStack identityOf(ItemStack stack) {
        return stack.isEmpty()
                ? ItemStack.EMPTY
                : stack.copyWithCount(1);
    }

    private static boolean sameIdentity(ItemStack first, ItemStack second) {
        return first.isEmpty() && second.isEmpty()
                || !first.isEmpty()
                && !second.isEmpty()
                && ItemStack.isSameItemSameComponents(
                        first,
                        identityOf(second)
                );
    }

    private void syncTool() {
        if (level != null) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case TOOL_SLOT -> isWorkshopTool(stack);
            case INPUT_SLOT -> true;
            case OUTPUT_SLOT -> false;
            default -> false;
        };
    }

    public static boolean isWorkshopTool(ItemStack stack) {
        return stack.is(ModTags.KNIVES)
                || stack.is(ModTags.HAMMERS)
                || stack.is(ModTags.SAWS);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previous = getItem(slot);
        boolean reset = slot != OUTPUT_SLOT
                && !sameIdentity(identityOf(previous), stack);
        super.setItem(slot, stack);
        if (reset) {
            resetProgress();
        }
        if (slot == TOOL_SLOT) {
            syncTool();
        }
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack removed = super.removeItem(slot, count);
        if (!removed.isEmpty() && slot != OUTPUT_SLOT) {
            resetProgress();
        }
        if (!removed.isEmpty() && slot == TOOL_SLOT) {
            syncTool();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        if (!removed.isEmpty() && slot != OUTPUT_SLOT) {
            resetProgress();
        }
        if (!removed.isEmpty() && slot == TOOL_SLOT) {
            syncTool();
        }
        return removed;
    }

    @Override
    public void clearContent() {
        super.clearContent();
        resetProgress();
        syncTool();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return false;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        progress = input.getIntOr("Progress", 0);
        maxProgress = input.getIntOr("MaxProgress", 0);
        activeRecipe = input.read(
                "ActiveRecipe",
                Recipe.KEY_CODEC
        ).orElse(null);
        activeToolIdentity = identityOf(items.get(TOOL_SLOT));
        activeInputIdentity = identityOf(items.get(INPUT_SLOT));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("Progress", progress);
        output.putInt("MaxProgress", maxProgress);
        if (activeRecipe != null) {
            output.store("ActiveRecipe", Recipe.KEY_CODEC, activeRecipe);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory
    ) {
        return new ManualWorkshopMenu(
                containerId,
                inventory,
                this,
                dataAccess
        );
    }

    public int progress() {
        return progress;
    }

    public int maxProgress() {
        return maxProgress;
    }
}
