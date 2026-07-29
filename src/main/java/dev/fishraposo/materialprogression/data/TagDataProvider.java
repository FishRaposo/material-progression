package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.data.PackOutput;

final class TagDataProvider extends GeneratedResourceProvider {
    TagDataProvider(PackOutput output) {
        super(
                "Material Progression tags",
                output.getOutputFolder(PackOutput.Target.DATA_PACK)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        MaterialFamily flint = MaterialFamilies.FLINT;
        MaterialFamily tin = MaterialFamilies.TIN;
        MaterialFamily bronze = MaterialFamilies.BRONZE;

        resources.put(
                "c/tags/block/ores.json",
                tag("#c:ores/" + tin.name())
        );
        resources.put(
                "c/tags/block/ores/" + tin.name() + ".json",
                tag(
                        itemId(tin, "ore"),
                        "material_progression:deepslate_" + tin.name() + "_ore"
                )
        );
        resources.put(
                "c/tags/item/dusts.json",
                tag(
                        "#c:dusts/" + bronze.name(),
                        "#c:dusts/copper",
                        "#c:dusts/" + tin.name()
                )
        );
        resources.put(
                "c/tags/item/dusts/" + bronze.name() + ".json",
                tag(itemId(bronze, "dust"))
        );
        resources.put(
                "c/tags/item/dusts/copper.json",
                tag("material_progression:copper_dust")
        );
        resources.put(
                "c/tags/item/dusts/" + tin.name() + ".json",
                tag(itemId(tin, "dust"))
        );
        resources.put(
                "c/tags/item/flint_shards.json",
                tag(itemId(flint, "shard"))
        );
        resources.put(
                "c/tags/item/fibers.json",
                tag("#c:fibers/plant")
        );
        resources.put(
                "c/tags/item/fibers/plant.json",
                tag("material_progression:plant_fiber")
        );
        resources.put(
                "c/tags/item/ingots.json",
                tag(
                        "#c:ingots/" + bronze.name(),
                        "#c:ingots/" + tin.name()
                )
        );
        resources.put(
                "c/tags/item/ingots/" + bronze.name() + ".json",
                tag(itemId(bronze, "ingot"))
        );
        resources.put(
                "c/tags/item/ingots/" + tin.name() + ".json",
                tag(itemId(tin, "ingot"))
        );
        resources.put(
                "c/tags/item/ores.json",
                tag("#c:ores/" + tin.name())
        );
        resources.put(
                "c/tags/item/ores/" + tin.name() + ".json",
                tag(
                        itemId(tin, "ore"),
                        "material_progression:deepslate_" + tin.name() + "_ore"
                )
        );
        resources.put(
                "c/tags/item/raw_materials.json",
                tag("#c:raw_materials/" + tin.name())
        );
        resources.put(
                "c/tags/item/raw_materials/" + tin.name() + ".json",
                tag("material_progression:raw_" + tin.name())
        );
        resources.put(
                "c/tags/item/rocks.json",
                tag("material_progression:rock")
        );
        resources.put(
                "c/tags/item/tools/knives.json",
                tag("material_progression:flint_knife")
        );

        for (MaterialFamily family : MaterialFamilies.ALL) {
            resources.put(
                    MaterialProgression.MOD_ID
                            + "/tags/block/"
                            + "incorrect_for_"
                            + family.name()
                            + "_tool"
                            + ".json",
                    tag("#" + family.inheritedIncorrectBlocksTag())
            );
        }
        resources.put(
                MaterialProgression.MOD_ID
                        + "/tags/item/crusher_inputs.json",
                tag(
                        "#c:ores/copper",
                        "#c:ores/" + tin.name(),
                        "#c:raw_materials/copper",
                        "#c:raw_materials/" + tin.name()
                )
        );
        resources.put(
                MaterialProgression.MOD_ID + "/tags/block/fiber_plants.json",
                tag(
                        "minecraft:short_grass",
                        "minecraft:tall_grass",
                        "minecraft:fern",
                        "minecraft:large_fern",
                        "minecraft:vine",
                        "#minecraft:leaves"
                )
        );

        resources.put(
                "minecraft/tags/block/mineable/pickaxe.json",
                additiveTag(
                        "material_progression:crusher",
                        itemId(tin, "ore"),
                        "material_progression:deepslate_" + tin.name() + "_ore"
                )
        );
        resources.put(
                "minecraft/tags/block/needs_stone_tool.json",
                additiveTag(
                        itemId(tin, "ore"),
                        "material_progression:deepslate_" + tin.name() + "_ore"
                )
        );

        for (ToolKind tool : List.of(
                ToolKind.AXE,
                ToolKind.HOE,
                ToolKind.PICKAXE,
                ToolKind.SHOVEL,
                ToolKind.SWORD
        )) {
            resources.put(
                    "minecraft/tags/item/" + tool.itemTags().getFirst() + ".json",
                    tag(toolItems(tool).toArray())
            );
        }
        resources.put(
                "minecraft/tags/item/enchantable/durability.json",
                additiveTag(allToolItems().toArray())
        );
        resources.put(
                "minecraft/tags/item/enchantable/mining.json",
                additiveTag(toolItemsExcept(ToolKind.SWORD).toArray())
        );
        resources.put(
                "minecraft/tags/item/enchantable/sharp_weapon.json",
                additiveTag(combatToolItems().toArray())
        );
        resources.put(
                "minecraft/tags/item/enchantable/weapon.json",
                additiveTag(weaponItems().toArray())
        );
        return resources;
    }

    private static List<Object> allToolItems() {
        List<Object> items = new ArrayList<>();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                items.add(family.itemId(tool));
            }
        }
        items.add("material_progression:flint_knife");
        return items;
    }

    private static List<Object> toolItems(ToolKind expected) {
        List<Object> items = new ArrayList<>();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                if (tool == expected
                        || expected == ToolKind.AXE && tool == ToolKind.HATCHET) {
                    items.add(family.itemId(tool));
                }
            }
        }
        return items;
    }

    private static List<Object> toolItemsExcept(ToolKind excluded) {
        List<Object> items = new ArrayList<>();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                if (tool != excluded) {
                    items.add(family.itemId(tool));
                }
            }
        }
        items.add("material_progression:flint_knife");
        return items;
    }

    private static List<Object> weaponItems() {
        List<Object> items = toolItems(ToolKind.SWORD);
        items.add("material_progression:flint_knife");
        return items;
    }

    private static List<Object> combatToolItems() {
        List<Object> items = new ArrayList<>();
        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                if (tool == ToolKind.SWORD || tool == ToolKind.AXE) {
                    items.add(family.itemId(tool));
                }
            }
        }
        items.add("material_progression:flint_knife");
        return items;
    }

    private static JsonObject tag(Object... values) {
        return DataJson.object("values", DataJson.array(values));
    }

    private static JsonObject additiveTag(Object... values) {
        return DataJson.object(
                "replace", false,
                "values", DataJson.array(values)
        );
    }

    private static String itemId(MaterialFamily family, String suffix) {
        return "material_progression:" + family.name() + "_" + suffix;
    }
}
