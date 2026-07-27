package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.world.inventory.CrusherMenu;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class CrusherScreen extends AbstractFurnaceScreen<CrusherMenu> {
    private static final Identifier LIT_PROGRESS =
            Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier PROCESS_PROGRESS =
            Identifier.withDefaultNamespace("container/furnace/burn_progress");
    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/furnace.png");
    private static final Component FILTER_NAME =
            Component.translatable("gui.material_progression.recipebook.toggle_crushable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE)
    );

    public CrusherScreen(CrusherMenu menu, Inventory inventory, Component title) {
        super(
                menu,
                inventory,
                title,
                FILTER_NAME,
                TEXTURE,
                LIT_PROGRESS,
                PROCESS_PROGRESS,
                TABS
        );
    }
}
