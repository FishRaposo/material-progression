package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class RecipeDataProvider extends GeneratedResourceProvider {
    RecipeDataProvider(PackOutput output) {
        super(
                "Material Progression recipes",
                output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();

        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                resources.put(
                        "recipe/" + family.itemPath(tool) + ".json",
                        toolRecipe(family, tool)
                );
            }
        }

        resources.put(
                "recipe/bronze_dust.json",
                DataJson.object(
                        "type", "minecraft:crafting_shapeless",
                        "category", "misc",
                        "ingredients", DataJson.array(
                                "#c:dusts/copper",
                                "#c:dusts/copper",
                                "#c:dusts/copper",
                                commonTag("dusts", MaterialFamilies.TIN)
                        ),
                        "result", result(
                                materialItem(MaterialFamilies.BRONZE, "dust"),
                                4
                        )
                )
        );
        resources.put(
                "recipe/cobblestone_from_rocks.json",
                DataJson.object(
                        "type", "minecraft:crafting_shapeless",
                        "ingredients", DataJson.array(
                                "#c:rocks",
                                "#c:rocks",
                                "#c:rocks",
                                "#c:rocks"
                        ),
                        "result", result("minecraft:cobblestone", 1)
                )
        );
        resources.put(
                "recipe/crusher.json",
                DataJson.object(
                        "type", "minecraft:crafting_shaped",
                        "category", "misc",
                        "key", DataJson.object(
                                "C", "minecraft:cobblestone",
                                "F", "minecraft:flint"
                        ),
                        "pattern", DataJson.array("CCC", "CFC", "CCC"),
                        "result", result("material_progression:crusher", 1)
                )
        );
        resources.put(
                "recipe/flint_shard_from_flint.json",
                DataJson.object(
                        "type", "minecraft:crafting_shapeless",
                        "ingredients", DataJson.array("minecraft:flint"),
                        "result", result("material_progression:flint_shard", 2)
                )
        );
        resources.put(
                "recipe/flint_shard_from_rock.json",
                DataJson.object(
                        "type", "minecraft:crafting_shapeless",
                        "ingredients", DataJson.array("#c:rocks"),
                        "result", result("material_progression:flint_shard", 1)
                )
        );
        resources.put(
                "recipe/flint_knife.json",
                DataJson.object(
                        "type", "minecraft:crafting_shaped",
                        "key", DataJson.object(
                                "R", "#c:rocks",
                                "S", "#c:rods/wooden"
                        ),
                        "pattern", DataJson.array("R", "S"),
                        "result", result(
                                "material_progression:flint_knife",
                                1
                        )
                )
        );
        resources.put(
                "recipe/string_from_plant_fiber.json",
                DataJson.object(
                        "type", "minecraft:crafting_shapeless",
                        "ingredients", DataJson.array(
                                "#c:fibers/plant",
                                "#c:fibers/plant",
                                "#c:fibers/plant"
                        ),
                        "result", result("minecraft:string", 1)
                )
        );

        resources.put(
                "recipe/crushing_copper_ore.json",
                crushing("#c:ores/copper", "material_progression:copper_dust")
        );
        resources.put(
                "recipe/crushing_raw_copper.json",
                crushing(
                        "#c:raw_materials/copper",
                        "material_progression:copper_dust"
                )
        );
        resources.put(
                "recipe/crushing_raw_tin.json",
                crushing(
                        commonTag("raw_materials", MaterialFamilies.TIN),
                        materialItem(MaterialFamilies.TIN, "dust")
                )
        );
        resources.put(
                "recipe/crushing_tin_ore.json",
                crushing(
                        commonTag("ores", MaterialFamilies.TIN),
                        materialItem(MaterialFamilies.TIN, "dust")
                )
        );

        resources.put(
                "recipe/manual_processing/knife_rock.json",
                manualProcessing(
                        "#c:tools/knives", "#c:rocks",
                        "material_progression:flint_shard", 2, 1, 20
                )
        );
        resources.put(
                "recipe/manual_processing/knife_leaves.json",
                manualProcessing(
                        "#c:tools/knives", "#minecraft:leaves",
                        "material_progression:plant_fiber", 2, 1, 20
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_stone.json",
                manualProcessing(
                        "#c:tools/hammers", "minecraft:stone",
                        "minecraft:gravel", 1, 2, 40
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_gravel.json",
                manualProcessing(
                        "#c:tools/hammers", "minecraft:gravel",
                        "minecraft:sand", 1, 2, 40
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_copper_ore.json",
                manualProcessing(
                        "#c:tools/hammers", "#c:ores/copper",
                        "material_progression:copper_dust", 2, 12, 100
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_raw_copper.json",
                manualProcessing(
                        "#c:tools/hammers", "#c:raw_materials/copper",
                        "material_progression:copper_dust", 2, 12, 100
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_tin_ore.json",
                manualProcessing(
                        "#c:tools/hammers", "#c:ores/tin",
                        "material_progression:tin_dust", 2, 12, 100
                )
        );
        resources.put(
                "recipe/manual_processing/hammer_raw_tin.json",
                manualProcessing(
                        "#c:tools/hammers", "#c:raw_materials/tin",
                        "material_progression:tin_dust", 2, 12, 100
                )
        );

        for (String[] wood : new String[][]{
                {"oak", "oak_log"},
                {"spruce", "spruce_log"},
                {"birch", "birch_log"},
                {"jungle", "jungle_log"},
                {"acacia", "acacia_log"},
                {"dark_oak", "dark_oak_log"},
                {"mangrove", "mangrove_log"},
                {"cherry", "cherry_log"},
                {"pale_oak", "pale_oak_log"},
                {"bamboo", "bamboo_block"},
                {"crimson", "crimson_stem"},
                {"warped", "warped_stem"}
        }) {
            resources.put(
                    "recipe/manual_processing/saw_" + wood[0] + "_log.json",
                    manualProcessing(
                            "#c:tools/saws", "minecraft:" + wood[1],
                            "minecraft:" + wood[0] + "_planks", 6, 2, 40
                    )
            );
            resources.put(
                    "recipe/manual_processing/saw_" + wood[0] + "_planks.json",
                    manualProcessing(
                            "#c:tools/saws", "minecraft:" + wood[0] + "_planks",
                            "minecraft:stick", 3, 1, 20
                    )
            );
        }

        resources.put(
                "recipe/smelting_bronze_dust.json",
                smelting(
                        commonTag("dusts", MaterialFamilies.BRONZE),
                        materialItem(MaterialFamilies.BRONZE, "ingot")
                )
        );
        resources.put(
                "recipe/smelting_copper_dust.json",
                smelting("#c:dusts/copper", "minecraft:copper_ingot")
        );
        resources.put(
                "recipe/smelting_raw_tin.json",
                smelting(
                        commonTag("raw_materials", MaterialFamilies.TIN),
                        materialItem(MaterialFamilies.TIN, "ingot")
                )
        );
        resources.put(
                "recipe/smelting_tin_dust.json",
                smelting(
                        commonTag("dusts", MaterialFamilies.TIN),
                        materialItem(MaterialFamilies.TIN, "ingot")
                )
        );
        return resources;
    }

    private static JsonObject toolRecipe(
            MaterialFamily family,
            ToolKind tool
    ) {
        if (tool == ToolKind.HATCHET) {
            return DataJson.object(
                    "type", "minecraft:crafting_shaped",
                    "pattern", DataJson.array("RS", " S"),
                    "key", DataJson.object(
                            "R", "#" + family.repairIngredient().location(),
                            "S", "#c:rods/wooden"
                    ),
                    "result", result(family.itemId(tool), 1)
            );
        }

        return DataJson.object(
                "type", "minecraft:crafting_shaped",
                "category", "equipment",
                "key", DataJson.object(
                        "#", "#c:rods/wooden",
                        "X", "#" + family.repairIngredient().location()
                ),
                "pattern", toolPattern(tool),
                "result", result(family.itemId(tool), 1)
        );
    }

    private static JsonElement toolPattern(ToolKind tool) {
        return switch (tool) {
            case AXE -> DataJson.array("XX", "X#", " #");
            case HOE -> DataJson.array("XX", " #", " #");
            case PICKAXE -> DataJson.array("XXX", " # ", " # ");
            case SHOVEL -> DataJson.array("X", "#", "#");
            case SWORD -> DataJson.array("X", "X", "#");
            case HATCHET -> throw new IllegalArgumentException(
                    "Hatchets use the primitive recipe"
            );
        };
    }

    private static JsonObject crushing(String ingredient, String output) {
        return DataJson.object(
                "type", "material_progression:crushing",
                "category", "misc",
                "cookingtime", 200,
                "experience", 0.0F,
                "ingredient", ingredient,
                "result", result(output, 2)
        );
    }

    private static JsonObject smelting(String ingredient, String output) {
        return DataJson.object(
                "type", "minecraft:smelting",
                "category", "misc",
                "cookingtime", 200,
                "experience", 0.7F,
                "ingredient", ingredient,
                "result", result(output, 1)
        );
    }

    private static JsonObject manualProcessing(
            String tool,
            String input,
            String output,
            int count,
            int durabilityCost,
            int operationTime
    ) {
        return DataJson.object(
                "type", "material_progression:manual_processing",
                "tool", tool,
                "input", input,
                "result", result(output, count),
                "durability_cost", durabilityCost,
                "operation_time", operationTime
        );
    }

    private static JsonObject result(String item, int count) {
        if (count == 1) {
            return DataJson.object("id", item);
        }
        return DataJson.object("count", count, "id", item);
    }

    private static String commonTag(String form, MaterialFamily family) {
        return "#c:" + form + "/" + family.name();
    }

    private static String materialItem(MaterialFamily family, String form) {
        return "material_progression:" + family.name() + "_" + form;
    }
}
