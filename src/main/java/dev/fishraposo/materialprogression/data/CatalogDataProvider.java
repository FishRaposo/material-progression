package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModItems;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class CatalogDataProvider extends GeneratedResourceProvider {
    CatalogDataProvider(PackOutput output) {
        super(
                "Material Progression declarative catalogs",
                output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        resources.put("material_family/catalog.json", materialFamilies());
        resources.put("creative_tab/main.json", creativeTab());
        return resources;
    }

    private static JsonObject materialFamilies() {
        JsonObject families = new JsonObject();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            families.add(
                    family.name(),
                    DataJson.object(
                            "durability", family.durability(),
                            "speed", family.speed(),
                            "attack_bonus", family.attackBonus(),
                            "enchantment_value", family.enchantmentValue(),
                            "repair_tag",
                            family.repairIngredient().location().toString(),
                            "incorrect_blocks_tag",
                            family.incorrectForTool().location().toString()
                    )
            );
        }
        return DataJson.object("families", families);
    }

    private static JsonObject creativeTab() {
        return DataJson.object(
                "items",
                DataJson.array(
                        ModItems.creativeTabContents().stream()
                                .map(item -> item.getId().toString())
                                .map(id -> id.substring(id.indexOf(':') + 1))
                                .toArray()
                )
        );
    }
}
