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
