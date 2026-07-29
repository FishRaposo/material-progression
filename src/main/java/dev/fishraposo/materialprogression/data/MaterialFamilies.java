package dev.fishraposo.materialprogression.data;

import dev.fishraposo.materialprogression.registry.ModTags;
import java.util.List;

public final class MaterialFamilies {
    public static final MaterialFamily FLINT = new MaterialFamily(
            "flint",
            ModTags.INCORRECT_FOR_FLINT_TOOL,
            64,
            5.0F,
            1.5F,
            5,
            ModTags.FLINT_SHARDS,
            "minecraft:incorrect_for_stone_tool",
            "stone",
            "Flint",
            "Sílex",
            List.of(ToolKind.HATCHET)
    );

    public static final MaterialFamily TIN = new MaterialFamily(
            "tin",
            ModTags.INCORRECT_FOR_TIN_TOOL,
            96,
            3.5F,
            0.5F,
            8,
            ModTags.INGOTS_TIN,
            "minecraft:incorrect_for_stone_tool",
            "iron",
            "Tin",
            "Estanho",
            List.of(
                    ToolKind.SWORD,
                    ToolKind.PICKAXE,
                    ToolKind.AXE,
                    ToolKind.SHOVEL,
                    ToolKind.HOE
            )
    );

    public static final MaterialFamily BRONZE = new MaterialFamily(
            "bronze",
            ModTags.INCORRECT_FOR_BRONZE_TOOL,
            325,
            6.5F,
            2.0F,
            12,
            ModTags.INGOTS_BRONZE,
            "minecraft:incorrect_for_iron_tool",
            "golden",
            "Bronze",
            "Bronze",
            List.of(
                    ToolKind.SWORD,
                    ToolKind.PICKAXE,
                    ToolKind.AXE,
                    ToolKind.SHOVEL,
                    ToolKind.HOE
            )
    );

    public static final List<MaterialFamily> ALL = List.of(FLINT, TIN, BRONZE);

    private MaterialFamilies() {
    }
}
