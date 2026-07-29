package dev.fishraposo.materialprogression.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class MaterialProgressionData {
    private MaterialProgressionData() {
    }

    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(RecipeDataProvider::new);
        event.createProvider(TagDataProvider::new);
        event.createProvider(ItemModelDataProvider::new);
        event.createProvider(BlockStateDataProvider::new);
        event.createProvider(LootDataProvider::new);
        event.createProvider(TranslationDataProvider::new);
        event.createProvider(CatalogDataProvider::new);
    }
}
