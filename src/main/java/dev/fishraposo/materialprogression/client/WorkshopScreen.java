package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.network.SelectWorkshopRecipePayload;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;

public final class WorkshopScreen extends AbstractContainerScreen<WorkshopMenu> {
    private int selectedIndex;

    public WorkshopScreen(
            WorkshopMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(
                Component.literal("<"),
                button -> selectRelative(-1)
        ).bounds(leftPos + 63, topPos + 58, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("gui.material_progression.workshop.process"),
                button -> {
                    selectCurrent();
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(
                                menu.containerId,
                                0
                        );
                    }
                }
        ).bounds(leftPos + 85, topPos + 58, 70, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal(">"),
                button -> selectRelative(1)
        ).bounds(leftPos + 157, topPos + 58, 20, 20).build());
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
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
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderLabels(graphics, mouseX, mouseY);
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                menu.matchingRecipes();
        Component selection = recipes.isEmpty()
                ? Component.translatable(
                        "gui.material_progression.workshop.no_recipe"
                )
                : recipes.get(Math.floorMod(selectedIndex, recipes.size()))
                        .value()
                        .result()
                        .create()
                        .getHoverName();
        graphics.drawCenteredString(
                font,
                selection,
                imageWidth / 2,
                20,
                0x404040
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void selectRelative(int offset) {
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                menu.matchingRecipes();
        if (recipes.isEmpty()) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.floorMod(selectedIndex + offset, recipes.size());
        selectCurrent();
    }

    private void selectCurrent() {
        List<RecipeHolder<ManualProcessingRecipe>> recipes =
                menu.matchingRecipes();
        if (recipes.isEmpty()) {
            return;
        }
        selectedIndex = Math.floorMod(selectedIndex, recipes.size());
        ClientPacketDistributor.sendToServer(
                new SelectWorkshopRecipePayload(
                        recipes.get(selectedIndex).id().identifier()
                )
        );
    }
}
