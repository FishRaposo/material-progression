package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class ItemModelDataProvider extends GeneratedResourceProvider {
    ItemModelDataProvider(PackOutput output) {
        super(
                "Material Progression item models",
                output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                resources.put(
                        "items/" + family.itemPath(tool) + ".json",
                        itemModel(family.itemModel(tool))
                );
            }
        }

        MaterialFamily flint = MaterialFamilies.FLINT;
        MaterialFamily tin = MaterialFamilies.TIN;
        MaterialFamily bronze = MaterialFamilies.BRONZE;
        resources.put(
                "items/" + flint.name() + "_shard.json",
                itemModel("minecraft:item/flint")
        );
        resources.put(
                "items/flint_knife.json",
                itemModel("minecraft:item/stone_sword")
        );
        resources.put(
                "items/plant_fiber.json",
                itemModel("minecraft:item/string")
        );
        resources.put("items/rock.json", itemModel("minecraft:item/cobblestone"));
        resources.put(
                "items/raw_" + tin.name() + ".json",
                itemModel("minecraft:item/raw_iron")
        );
        resources.put(
                "items/" + tin.name() + "_dust.json",
                itemModel("minecraft:item/sugar")
        );
        resources.put(
                "items/" + tin.name() + "_ingot.json",
                itemModel("minecraft:item/iron_ingot")
        );
        resources.put(
                "items/copper_dust.json",
                itemModel("minecraft:item/redstone")
        );
        resources.put(
                "items/" + bronze.name() + "_dust.json",
                itemModel("minecraft:item/blaze_powder")
        );
        resources.put(
                "items/" + bronze.name() + "_ingot.json",
                itemModel("minecraft:item/copper_ingot")
        );

        resources.put(
                "items/crusher.json",
                itemModel("minecraft:block/furnace")
        );
        resources.put(
                "items/" + tin.name() + "_ore.json",
                itemModel("minecraft:block/iron_ore")
        );
        resources.put(
                "items/deepslate_" + tin.name() + "_ore.json",
                itemModel("minecraft:block/deepslate_iron_ore")
        );
        resources.put(
                "items/loose_rocks.json",
                itemModel("material_progression:block/loose_rocks")
        );
        resources.put(
                "items/ground_stick.json",
                itemModel("material_progression:block/ground_stick")
        );
        return resources;
    }

    private static JsonElement itemModel(String model) {
        return DataJson.object(
                "model", DataJson.object(
                        "type", "minecraft:model",
                        "model", model
                )
        );
    }
}
