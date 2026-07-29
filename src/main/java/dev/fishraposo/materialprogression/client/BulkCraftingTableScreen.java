package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.network.BulkCraftingPreviewPayload;
import dev.fishraposo.materialprogression.network.ExecuteBulkCraftingPayload;
import dev.fishraposo.materialprogression.network.RequestBulkCraftingPreviewPayload;
import dev.fishraposo.materialprogression.world.inventory.BulkCraftingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class BulkCraftingTableScreen
        extends AbstractContainerScreen<BulkCraftingTableMenu> {
    private @Nullable EditBox search;
    private int selectedIndex;
    private int requested = 1;
    private String lastQuery = "";
    private String lastTarget = "";
    private int lastRequested;

    public BulkCraftingTableScreen(
            BulkCraftingTableMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, 252, 166);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        ClientBulkCraftingPreviews.clear();
        search = addRenderableWidget(new EditBox(
                font,
                leftPos + 180,
                topPos + 18,
                64,
                18,
                Component.translatable(
                        "gui.material_progression.bulk_crafting_table.search"
                )
        ));
        search.setMaxLength(64);
        addRenderableWidget(Button.builder(
                Component.literal("<"),
                button -> selectRelative(-1)
        ).bounds(leftPos + 180, topPos + 40, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal("-"),
                button -> changeQuantity(-1)
        ).bounds(leftPos + 202, topPos + 40, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal("+"),
                button -> changeQuantity(1)
        ).bounds(leftPos + 224, topPos + 40, 20, 20).build());
        addRenderableWidget(Button.builder(
                Component.translatable(
                        "gui.material_progression.bulk_crafting_table.craft"
                ),
                button -> executePreview()
        ).bounds(leftPos + 180, topPos + 62, 64, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal(">"),
                button -> selectRelative(1)
        ).bounds(leftPos + 224, topPos + 84, 20, 20).build());
        requestPreview();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        String query = search == null ? "" : search.getValue();
        String target = selectedTarget();
        if (!query.equals(lastQuery)
                || !target.equals(lastTarget)
                || requested != lastRequested) {
            requestPreview();
        }
    }

    @Override
    public void removed() {
        ClientBulkCraftingPreviews.clear();
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
        graphics.fill(
                leftPos + 176,
                topPos + 7,
                leftPos + imageWidth - 7,
                topPos + imageHeight - 7,
                0xFFB4B4B4
        );
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        super.extractLabels(graphics, mouseX, mouseY);
        BulkCraftingPreviewPayload preview =
                ClientBulkCraftingPreviews.get(menu.containerId);
        String target = selectedTarget();
        Component targetName = target.isEmpty()
                ? Component.translatable(
                        "gui.material_progression.bulk_crafting_table.no_recipe"
                )
                : new ItemStack(BuiltInRegistries.ITEM
                        .getValue(Identifier.parse(target)))
                        .getHoverName();
        graphics.centeredText(font, targetName, 212, 108, 0x404040);
        String summary = preview == null
                ? "x" + requested
                : "x" + requested + "/" + preview.maxQuantity()
                        + "  -" + total(preview.costs())
                        + " +" + (requested + total(preview.surplus()));
        graphics.centeredText(
                font,
                Component.literal(summary),
                212,
                122,
                preview != null && !preview.executable()
                        ? 0xA00000
                        : 0x404040
        );
        if (preview != null && !preview.failure().isEmpty()) {
            graphics.centeredText(
                    font,
                    Component.literal(preview.failure()),
                    212,
                    136,
                    0xA00000
            );
        }
    }

    private void selectRelative(int change) {
        BulkCraftingPreviewPayload preview =
                ClientBulkCraftingPreviews.get(menu.containerId);
        if (preview == null || preview.targets().isEmpty()) {
            selectedIndex = 0;
            return;
        }
        selectedIndex = Math.floorMod(
                selectedIndex + change,
                preview.targets().size()
        );
        requestPreview();
    }

    private void changeQuantity(int change) {
        requested = Math.clamp(requested + change, 1, 64);
        requestPreview();
    }

    private void executePreview() {
        BulkCraftingPreviewPayload preview =
                ClientBulkCraftingPreviews.get(menu.containerId);
        if (preview == null || !preview.executable()
                || !preview.target().equals(selectedTarget())) {
            return;
        }
        ClientPacketDistributor.sendToServer(
                new ExecuteBulkCraftingPayload(
                        menu.containerId,
                        preview.target(),
                        preview.requested(),
                        preview.fingerprint(),
                        preview.sequence()
                )
        );
    }

    private void requestPreview() {
        String query = search == null ? "" : search.getValue();
        String target = selectedTarget();
        lastQuery = query;
        lastTarget = target;
        lastRequested = requested;
        ClientPacketDistributor.sendToServer(
                new RequestBulkCraftingPreviewPayload(
                        menu.containerId,
                        query,
                        target,
                        requested
                )
        );
    }

    private String selectedTarget() {
        BulkCraftingPreviewPayload preview =
                ClientBulkCraftingPreviews.get(menu.containerId);
        if (preview == null || preview.targets().isEmpty()) {
            return "";
        }
        selectedIndex = Math.floorMod(
                selectedIndex,
                preview.targets().size()
        );
        return preview.targets().get(selectedIndex);
    }

    private static int total(
            java.util.List<dev.fishraposo.materialprogression.network.PreviewStack>
                    stacks
    ) {
        return stacks.stream().mapToInt(stack -> stack.count()).sum();
    }
}
