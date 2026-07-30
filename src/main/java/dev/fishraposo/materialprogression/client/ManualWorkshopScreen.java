package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.world.inventory.ManualWorkshopMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class ManualWorkshopScreen
        extends AbstractContainerScreen<ManualWorkshopMenu> {
    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace(
                    "textures/gui/container/furnace.png"
            );
    private static final Identifier PROGRESS =
            Identifier.withDefaultNamespace(
                    "container/furnace/burn_progress"
            );

    public ManualWorkshopScreen(
            ManualWorkshopMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos,
                topPos,
                0.0F,
                0.0F,
                imageWidth,
                imageHeight,
                256,
                256
        );
        int width = menu.progressWidth(24);
        if (width > 0) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    PROGRESS,
                    24,
                    16,
                    0,
                    0,
                    leftPos + 79,
                    topPos + 34,
                    width,
                    16
            );
        }
    }
}
