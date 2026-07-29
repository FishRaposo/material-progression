package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class MaterialProgressionData {
    private MaterialProgressionData() {
    }

    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(MaterialFamilyCatalogProvider::new);
    }

    private static final class MaterialFamilyCatalogProvider implements DataProvider {
        private final Path output;

        private MaterialFamilyCatalogProvider(PackOutput output) {
            this.output = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(MaterialProgression.MOD_ID)
                    .resolve("material_family")
                    .resolve("catalog.json");
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            JsonObject families = new JsonObject();
            for (MaterialFamily family : MaterialFamilies.ALL) {
                JsonObject encoded = new JsonObject();
                encoded.addProperty("durability", family.durability());
                encoded.addProperty("speed", family.speed());
                encoded.addProperty("attack_bonus", family.attackBonus());
                encoded.addProperty("enchantment_value", family.enchantmentValue());
                encoded.addProperty("repair_tag", family.repairIngredient().location().toString());
                encoded.addProperty(
                        "incorrect_blocks_tag",
                        family.incorrectForTool().location().toString()
                );
                families.add(family.name(), encoded);
            }
            JsonObject root = new JsonObject();
            root.add("families", families);
            return DataProvider.saveStable(cache, root, output);
        }

        @Override
        public String getName() {
            return "Material Progression material family catalog";
        }
    }
}
