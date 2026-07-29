package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.planner.CraftingPlan;
import dev.fishraposo.materialprogression.planner.CraftingPlanner;
import dev.fishraposo.materialprogression.planner.MinecraftRecipeGraph;
import dev.fishraposo.materialprogression.planner.PlanRequest;
import dev.fishraposo.materialprogression.planner.RecipeGraph;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.transaction.BulkCraftingTransaction;
import dev.fishraposo.materialprogression.transaction.InventoryView;
import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import dev.fishraposo.materialprogression.world.item.BulkCraftingUpgradeItem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.server.level.ServerLevel;
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
    private long inventoryRevision;
    private final Set<String> filters = new LinkedHashSet<>();
    private final Map<String, Integer> reservations = new TreeMap<>();
    private final List<String> rememberedJobs = new ArrayList<>();
    private final InventoryView tableInventory = new InventoryView() {
        @Override
        public ItemStack getItem(int slot) {
            return BulkCraftingTableBlockEntity.this.getItem(slot);
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
            return stack.isEmpty() || (slot < activeBufferSlots()
                    && !(stack.getItem()
                            instanceof BulkCraftingUpgradeItem)
                    && stack.getCount() <= Math.min(
                            getMaxStackSize(),
                            stack.getMaxStackSize()
                    ));
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            BulkCraftingTableBlockEntity.this.setItem(slot, stack);
        }
    };

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

    public List<String> searchableTargets(String query) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        String needle = query == null ? "" : query.strip().toLowerCase();
        return MinecraftRecipeGraph.snapshot(serverLevel)
                .outputs()
                .stream()
                .filter(this::allowsTarget)
                .filter(item -> item.toLowerCase().contains(needle))
                .toList();
    }

    public @Nullable BulkPreview preview(
            Inventory playerInventory,
            String target,
            int quantity
    ) {
        if (!(level instanceof ServerLevel serverLevel)
                || quantity <= 0
                || quantity > 64
                || !allowsTarget(target)) {
            return null;
        }
        RecipeGraph graph = MinecraftRecipeGraph.snapshot(serverLevel);
        SourceBundle bundle = sourceBundle(playerInventory);
        CraftingPlan plan = new CraftingPlanner(graph).plan(
                new PlanRequest(
                        target,
                        quantity,
                        bundle.available,
                        new PlanRequest.Limits(12, 512, 64)
                )
        );
        BulkCraftingTransaction.Preview transactionPreview =
                bundle.transaction.simulate(plan, target, quantity);
        return new BulkPreview(
                target,
                quantity,
                plan,
                transactionPreview,
                maxQuantity(graph, bundle, target)
        );
    }

    public boolean execute(
            Inventory playerInventory,
            String target,
            int quantity,
            long expectedFingerprint
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        BulkPreview preview = preview(playerInventory, target, quantity);
        if (preview == null
                || !preview.executable()
                || preview.fingerprint() != expectedFingerprint) {
            return false;
        }
        SourceBundle bundle = sourceBundle(playerInventory);
        CraftingPlan plan = new CraftingPlanner(
                MinecraftRecipeGraph.snapshot(serverLevel)
        ).plan(new PlanRequest(
                target,
                quantity,
                bundle.available,
                new PlanRequest.Limits(12, 512, 64)
        ));
        BulkCraftingTransaction.Preview exact =
                bundle.transaction.simulate(plan, target, quantity);
        if (!exact.accepted()
                || exact.fingerprint() != expectedFingerprint) {
            return false;
        }
        boolean committed = bundle.transaction.commit(exact);
        if (committed) {
            selectedRecipe = target;
            rememberJob(target);
            setChanged();
        }
        return committed;
    }

    public boolean addFilter(String itemId) {
        if (moduleCapacity(BulkCraftingUpgradeItem.Family.FILTER) <= 0
                || itemId == null
                || itemId.isBlank()
                || filters.size() >= moduleCapacity(
                        BulkCraftingUpgradeItem.Family.FILTER
                ) * 9) {
            return false;
        }
        boolean changed = filters.add(itemId);
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public Set<String> filters() {
        return Set.copyOf(filters);
    }

    public boolean reserve(String itemId, int count) {
        if (moduleCapacity(
                BulkCraftingUpgradeItem.Family.RESERVATION
        ) <= 0 || itemId == null || itemId.isBlank() || count < 0) {
            return false;
        }
        if (count == 0) {
            reservations.remove(itemId);
        } else {
            reservations.put(itemId, count);
        }
        setChanged();
        return true;
    }

    public Map<String, Integer> reservations() {
        return Map.copyOf(reservations);
    }

    public List<String> rememberedJobs() {
        return List.copyOf(rememberedJobs);
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
        inventoryRevision = 0;
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

    private boolean allowsTarget(String target) {
        return target != null
                && !target.isBlank()
                && (filters.isEmpty() || filters.contains(target));
    }

    private int maxQuantity(
            RecipeGraph graph,
            SourceBundle bundle,
            String target
    ) {
        int maximum = 0;
        for (int quantity = 1; quantity <= 64; quantity++) {
            CraftingPlan plan = new CraftingPlanner(graph).plan(
                    new PlanRequest(
                            target,
                            quantity,
                            bundle.available,
                            new PlanRequest.Limits(12, 512, 64)
                    )
            );
            if (!plan.successful()
                    || !bundle.transaction
                            .simulate(plan, target, quantity)
                            .accepted()) {
                break;
            }
            maximum = quantity;
        }
        return maximum;
    }

    private SourceBundle sourceBundle(Inventory playerInventory) {
        List<BulkCraftingTransaction.Slice> internal =
                List.of(new BulkCraftingTransaction.Slice(
                        tableInventory,
                        java.util.stream.IntStream
                                .range(0, activeBufferSlots())
                                .boxed()
                                .toList()
                ));
        List<BulkCraftingTransaction.Slice> adjacent =
                adjacentSources();
        BulkCraftingTransaction.Slice player =
                new BulkCraftingTransaction.Slice(
                        InventoryView.of(playerInventory),
                        java.util.stream.IntStream
                                .range(0, playerInventory.getContainerSize())
                                .boxed()
                                .toList()
                );
        List<BulkCraftingTransaction.Slice> ordered = new ArrayList<>(internal);
        if (moduleCapacity(
                BulkCraftingUpgradeItem.Family.PRIORITY
        ) > 0) {
            ordered.addAll(adjacent);
            ordered.add(player);
        } else {
            ordered.add(player);
            ordered.addAll(adjacent);
        }
        Map<String, Integer> available = inventoryCounts(ordered);
        reservations.forEach((item, reserved) ->
                available.computeIfPresent(
                        item,
                        (ignored, count) -> Math.max(0, count - reserved)
                ));
        available.values().removeIf(count -> count <= 0);
        BulkCraftingTransaction.Slice output = internal.getFirst();
        return new SourceBundle(
                new BulkCraftingTransaction(ordered, output),
                Map.copyOf(available)
        );
    }

    private List<BulkCraftingTransaction.Slice> adjacentSources() {
        if (!(level instanceof ServerLevel)) {
            return List.of();
        }
        List<BulkCraftingTransaction.Slice> result = new ArrayList<>();
        for (Direction direction : List.of(
                Direction.UP,
                Direction.DOWN,
                Direction.NORTH,
                Direction.SOUTH,
                Direction.WEST,
                Direction.EAST
        )) {
            if (level.getBlockEntity(
                    getBlockPos().relative(direction)
            ) instanceof net.minecraft.world.Container container) {
                InventoryView view = InventoryView.of(container);
                result.add(new BulkCraftingTransaction.Slice(
                        view,
                        java.util.stream.IntStream
                                .range(0, view.size())
                                .boxed()
                                .toList()
                ));
            }
        }
        return List.copyOf(result);
    }

    private static Map<String, Integer> inventoryCounts(
            List<BulkCraftingTransaction.Slice> sources
    ) {
        Map<String, Integer> counts = new TreeMap<>();
        for (BulkCraftingTransaction.Slice source : sources) {
            for (int slot : source.slots()) {
                ItemStack stack = source.view().getItem(slot);
                if (!stack.isEmpty()) {
                    counts.merge(
                            BuiltInRegistries.ITEM
                                    .getKey(stack.getItem())
                                    .toString(),
                            stack.getCount(),
                            Integer::sum
                    );
                }
            }
        }
        return counts;
    }

    private void rememberJob(String target) {
        int capacity = moduleCapacity(
                BulkCraftingUpgradeItem.Family.MEMORY
        );
        if (capacity <= 0) {
            return;
        }
        rememberedJobs.remove(target);
        rememberedJobs.addFirst(target);
        while (rememberedJobs.size() > capacity) {
            rememberedJobs.removeLast();
        }
    }

    public record BulkPreview(
            String target,
            int requested,
            CraftingPlan plan,
            BulkCraftingTransaction.Preview transaction,
            int maxQuantity
    ) {
        public boolean executable() {
            return plan.successful() && transaction.accepted();
        }

        public long fingerprint() {
            return transaction.fingerprint();
        }

        public String failure() {
            return plan.failure()
                    .map(failure -> failure.reason().name().toLowerCase())
                    .orElse(transaction.failure());
        }
    }

    private record SourceBundle(
            BulkCraftingTransaction transaction,
            Map<String, Integer> available
    ) {
    }
}
