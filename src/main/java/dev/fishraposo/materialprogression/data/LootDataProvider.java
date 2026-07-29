package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class LootDataProvider extends GeneratedResourceProvider {
    LootDataProvider(PackOutput output) {
        super(
                "Material Progression block loot",
                output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        MaterialFamily tin = MaterialFamilies.TIN;
        resources.put(
                "loot_table/blocks/crusher.json",
                selfDrop("material_progression:crusher")
        );
        resources.put(
                "loot_table/blocks/" + tin.name() + "_ore.json",
                oreDrop("material_progression:raw_" + tin.name())
        );
        resources.put(
                "loot_table/blocks/deepslate_" + tin.name() + "_ore.json",
                oreDrop("material_progression:raw_" + tin.name())
        );
        resources.put(
                "loot_table/blocks/ground_stick.json",
                explosionDecayDrop("minecraft:stick")
        );
        resources.put(
                "loot_table/blocks/loose_rocks.json",
                explosionDecayDrop("material_progression:rock")
        );
        return resources;
    }

    private static JsonObject selfDrop(String item) {
        return DataJson.object(
                "type", "minecraft:block",
                "pools", DataJson.array(
                        DataJson.object(
                                "rolls", 1,
                                "entries", DataJson.array(
                                        DataJson.object(
                                                "type", "minecraft:item",
                                                "name", item
                                        )
                                ),
                                "conditions", DataJson.array(
                                        DataJson.object(
                                                "condition",
                                                "minecraft:survives_explosion"
                                        )
                                )
                        )
                )
        );
    }

    private static JsonObject oreDrop(String item) {
        return DataJson.object(
                "type", "minecraft:block",
                "pools", DataJson.array(
                        DataJson.object(
                                "rolls", 1,
                                "entries", DataJson.array(
                                        DataJson.object(
                                                "type", "minecraft:item",
                                                "name", item,
                                                "functions", DataJson.array(
                                                        DataJson.object(
                                                                "function",
                                                                "minecraft:apply_bonus",
                                                                "enchantment",
                                                                "minecraft:fortune",
                                                                "formula",
                                                                "minecraft:ore_drops"
                                                        ),
                                                        DataJson.object(
                                                                "function",
                                                                "minecraft:explosion_decay"
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static JsonObject explosionDecayDrop(String item) {
        return DataJson.object(
                "type", "minecraft:block",
                "pools", DataJson.array(
                        DataJson.object(
                                "rolls", 1,
                                "entries", DataJson.array(
                                        DataJson.object(
                                                "type", "minecraft:item",
                                                "name", item
                                        )
                                ),
                                "functions", DataJson.array(
                                        DataJson.object(
                                                "function",
                                                "minecraft:explosion_decay"
                                        )
                                )
                        )
                )
        );
    }
}
