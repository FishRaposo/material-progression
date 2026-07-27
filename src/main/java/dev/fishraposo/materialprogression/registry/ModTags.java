package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Block> INCORRECT_FOR_TIN_TOOL =
            blockTag("incorrect_for_tin_tool");
    public static final TagKey<Block> INCORRECT_FOR_BRONZE_TOOL =
            blockTag("incorrect_for_bronze_tool");

    public static final TagKey<Item> REPAIRS_TIN_TOOLS =
            itemTag("repairs_tin_tools");
    public static final TagKey<Item> REPAIRS_BRONZE_TOOLS =
            itemTag("repairs_bronze_tools");
    public static final TagKey<Item> CRUSHER_INPUTS =
            itemTag("crusher_inputs");

    private ModTags() {
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(
                Registries.BLOCK,
                Identifier.fromNamespaceAndPath(MaterialProgression.MOD_ID, path)
        );
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(MaterialProgression.MOD_ID, path)
        );
    }
}
