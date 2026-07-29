package dev.fishraposo.materialprogression.data;

import java.util.Objects;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public record MaterialFamily(
        String name,
        TagKey<Block> incorrectForTool,
        int durability,
        float speed,
        float attackBonus,
        int enchantmentValue,
        TagKey<Item> repairIngredient,
        String inheritedIncorrectBlocksTag,
        String vanillaModelMaterial,
        String englishName,
        String portugueseName,
        List<ToolKind> tools
) {
    public MaterialFamily {
        Objects.requireNonNull(name);
        Objects.requireNonNull(incorrectForTool);
        Objects.requireNonNull(repairIngredient);
        Objects.requireNonNull(inheritedIncorrectBlocksTag);
        Objects.requireNonNull(vanillaModelMaterial);
        Objects.requireNonNull(englishName);
        Objects.requireNonNull(portugueseName);
        tools = List.copyOf(tools);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Material family name cannot be blank");
        }
        if (durability <= 0 || speed <= 0.0F || enchantmentValue < 0) {
            throw new IllegalArgumentException("Invalid tool balance for " + name);
        }
    }

    public String itemPath(ToolKind tool) {
        return name + "_" + tool.itemSuffix();
    }

    public String itemId(ToolKind tool) {
        return "material_progression:" + itemPath(tool);
    }

    public String itemModel(ToolKind tool) {
        return "minecraft:item/" + vanillaModelMaterial + "_"
                + tool.vanillaModelSuffix();
    }

    public String englishToolName(ToolKind tool) {
        return englishName + " " + tool.englishName();
    }

    public String portugueseToolName(ToolKind tool) {
        return tool.portugueseName() + " de " + portugueseName;
    }

    public ToolMaterial toolMaterial() {
        return new ToolMaterial(
                incorrectForTool,
                durability,
                speed,
                attackBonus,
                enchantmentValue,
                repairIngredient
        );
    }
}
