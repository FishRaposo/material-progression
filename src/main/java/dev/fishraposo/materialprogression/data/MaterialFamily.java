package dev.fishraposo.materialprogression.data;

import java.util.Objects;
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
        TagKey<Item> repairIngredient
) {
    public MaterialFamily {
        Objects.requireNonNull(name);
        Objects.requireNonNull(incorrectForTool);
        Objects.requireNonNull(repairIngredient);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Material family name cannot be blank");
        }
        if (durability <= 0 || speed <= 0.0F || enchantmentValue < 0) {
            throw new IllegalArgumentException("Invalid tool balance for " + name);
        }
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
