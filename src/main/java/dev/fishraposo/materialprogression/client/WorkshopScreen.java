package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.network.ExecuteWorkshopBatchPayload;
import dev.fishraposo.materialprogression.network.RequestWorkshopPreviewPayload;
import dev.fishraposo.materialprogression.network.SelectWorkshopRecipePayload;
import dev.fishraposo.materialprogression.network.WorkshopPreviewPayload;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class WorkshopScreen extends AbstractContainerScreen<WorkshopMenu> {
    private int selectedIndex;
    private int requested = 1;
    private ItemStack lastTool = ItemStack.EMPTY;
    private ItemStack lastInput = ItemStack.EMPTY;
    private @Nullable Identifier lastRecipeId;
    private int lastRequested;

    public WorkshopScreen(
            WorkshopMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, 176, 166);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        ClientWorkshopPreviews.clear();
        addRenderableWidget(Button.builder(
                Component.literal("<"),
                button -> selectRelative(-1)
        ).bounds(leftPos + 7, topPos + 58, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal("-"),
                button -> changeQuantity(-1)
        ).bounds(leftPos + 29, topPos + 58, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal("+"),
                button -> changeQuantity(1)
        ).bounds(leftPos + 51, topPos + 58, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("gui.material_progression.workshop.process"),
                button -> executePreview()
        ).bounds(leftPos + 73, topPos + 58, 70, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal(">"),
                button -> selectRelative(1)
        ).bounds(leftPos + 145, topPos + 58, 20, 20).build());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        requestPreviewIfChanged();
    }

    @Override
    public void removed() {
        ClientWorkshopPreviews.clear();
        super.removed();
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.fill(
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFFC6C6C6
        );
        graphics.fill(
                leftPos + 7,
                topPos + 17,
                leftPos + imageWidth - 7,
                topPos + imageHeight - 7,
                0xFF8B8B8B
        );
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        super.extractLabels(graphics, mouseX, mouseY);
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                matchingRecipes();
        Component selection = recipes.isEmpty()
                ? Component.translatable(
                        "gui.material_progression.workshop.no_recipe"
                )
                : recipes.get(Math.floorMod(selectedIndex, recipes.size()))
                        .value()
                        .result()
                        .create()
                        .getHoverName();
        graphics.centeredText(
                font,
                selection,
                imageWidth / 2,
                20,
                0x404040
        );
        WorkshopPreviewPayload preview =
                ClientWorkshopPreviews.get(menu.containerId);
        int remainderCount = preview == null
                ? 0
                : preview.remainders().stream()
                        .mapToInt(remainder -> remainder.count())
                        .sum();
        Component summary = preview == null
                ? Component.literal("x" + requested)
                : Component.literal(
                        "x" + preview.executable()
                                + "/" + preview.requested()
                                + "  "
                                + preview.consumed().count()
                                + " -> "
                                + preview.produced().count()
                                + (remainderCount == 0
                                        ? ""
                                        : " +" + remainderCount + "r")
                                + "  -" + preview.durabilityCost()
                );
        graphics.centeredText(
                font,
                summary,
                imageWidth / 2,
                47,
                preview != null && !preview.exact()
                        ? 0xA00000
                        : 0x404040
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }

    private void selectRelative(int offset) {
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                matchingRecipes();
        if (recipes.isEmpty()) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.floorMod(selectedIndex + offset, recipes.size());
        selectCurrent();
    }

    private void selectCurrent() {
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                matchingRecipes();
        if (recipes.isEmpty()) {
            return;
        }
        selectedIndex = Math.floorMod(selectedIndex, recipes.size());
        ClientPacketDistributor.sendToServer(
                new SelectWorkshopRecipePayload(
                        recipes.get(selectedIndex).id().identifier()
                )
        );
        requestPreview(recipes.get(selectedIndex).id().identifier());
    }

    private void changeQuantity(int change) {
        requested = Math.clamp(requested + change, 1, 64);
        Identifier recipeId = selectedRecipeId();
        if (recipeId != null) {
            requestPreview(recipeId);
        }
    }

    private void executePreview() {
        WorkshopPreviewPayload preview =
                ClientWorkshopPreviews.get(menu.containerId);
        Identifier recipeId = selectedRecipeId();
        if (preview == null
                || recipeId == null
                || !preview.recipeId().equals(recipeId)
                || preview.requested() != requested
                || !preview.exact()) {
            return;
        }
        ClientPacketDistributor.sendToServer(
                new ExecuteWorkshopBatchPayload(
                        menu.containerId,
                        recipeId,
                        preview.requested(),
                        preview.revision(),
                        preview.sequence()
                )
        );
    }

    private void requestPreviewIfChanged() {
        Identifier recipeId = selectedRecipeId();
        if (recipeId == null) {
            ClientWorkshopPreviews.clear();
            return;
        }
        ItemStack tool = menu.toolStack();
        ItemStack input = menu.inputStack();
        if (!sameStack(lastTool, tool)
                || !sameStack(lastInput, input)
                || !recipeId.equals(lastRecipeId)
                || requested != lastRequested) {
            requestPreview(recipeId);
        }
    }

    private void requestPreview(Identifier recipeId) {
        lastTool = menu.toolStack().copy();
        lastInput = menu.inputStack().copy();
        lastRecipeId = recipeId;
        lastRequested = requested;
        ClientPacketDistributor.sendToServer(
                new RequestWorkshopPreviewPayload(
                        menu.containerId,
                        recipeId,
                        requested
                )
        );
    }

    private @Nullable Identifier selectedRecipeId() {
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                matchingRecipes();
        if (recipes.isEmpty()) {
            return null;
        }
        selectedIndex = Math.floorMod(selectedIndex, recipes.size());
        return recipes.get(selectedIndex).id().identifier();
    }

    private static boolean sameStack(ItemStack left, ItemStack right) {
        return left.getCount() == right.getCount()
                && ItemStack.isSameItemSameComponents(left, right);
    }

    private List<RecipeHolder<ManualProcessingRecipe>> matchingRecipes() {
        return ClientManualRecipes.matching(
                menu.toolStack(),
                menu.inputStack()
        );
    }
}
