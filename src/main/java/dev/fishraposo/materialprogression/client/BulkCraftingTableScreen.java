package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BulkCraftingTableScreen
        extends AbstractContainerScreen<BulkCraftingTableMenu> {
    public BulkCraftingTableScreen(
            BulkCraftingTableMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, 176, 166);
        inventoryLabelY = imageHeight - 94;
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
}
