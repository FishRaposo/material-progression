package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class BlockStateDataProvider extends GeneratedResourceProvider {
    BlockStateDataProvider(PackOutput output) {
        super(
                "Material Progression blockstates",
                output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        MaterialFamily tin = MaterialFamilies.TIN;
        resources.put(
                "blockstates/crusher.json",
                DataJson.object(
                        "variants", DataJson.object(
                                "facing=north,lit=false", model(
                                        "minecraft:block/furnace", 0
                                ),
                                "facing=east,lit=false", model(
                                        "minecraft:block/furnace", 90
                                ),
                                "facing=south,lit=false", model(
                                        "minecraft:block/furnace", 180
                                ),
                                "facing=west,lit=false", model(
                                        "minecraft:block/furnace", 270
                                ),
                                "facing=north,lit=true", model(
                                        "minecraft:block/furnace_on", 0
                                ),
                                "facing=east,lit=true", model(
                                        "minecraft:block/furnace_on", 90
                                ),
                                "facing=south,lit=true", model(
                                        "minecraft:block/furnace_on", 180
                                ),
                                "facing=west,lit=true", model(
                                        "minecraft:block/furnace_on", 270
                                )
                        )
                )
        );
        resources.put(
                "blockstates/workshop.json",
                simpleBlockstate("minecraft:block/crafting_table")
        );
        resources.put(
                "blockstates/bulk_crafting_table.json",
                simpleBlockstate("minecraft:block/crafting_table")
        );
        resources.put(
                "blockstates/" + tin.name() + "_ore.json",
                simpleBlockstate("minecraft:block/iron_ore")
        );
        resources.put(
                "blockstates/deepslate_" + tin.name() + "_ore.json",
                simpleBlockstate("minecraft:block/deepslate_iron_ore")
        );
        resources.put(
                "blockstates/loose_rocks.json",
                rotatedBlockstate("material_progression:block/loose_rocks")
        );
        resources.put(
                "blockstates/ground_stick.json",
                rotatedBlockstate("material_progression:block/ground_stick")
        );
        return resources;
    }

    private static JsonObject simpleBlockstate(String model) {
        return DataJson.object(
                "variants", DataJson.object("", DataJson.object("model", model))
        );
    }

    private static JsonObject rotatedBlockstate(String model) {
        return DataJson.object(
                "variants", DataJson.object(
                        "", DataJson.array(
                                model(model, 0),
                                model(model, 90),
                                model(model, 180),
                                model(model, 270)
                        )
                )
        );
    }

    private static JsonObject model(String model, int rotation) {
        if (rotation == 0) {
            return DataJson.object("model", model);
        }
        return DataJson.object("model", model, "y", rotation);
    }
}
