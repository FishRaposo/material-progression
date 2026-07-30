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
    public static final TagKey<Block> INCORRECT_FOR_FLINT_TOOL =
            blockTag("incorrect_for_flint_tool");
    public static final TagKey<Block> LOOSE_ROCK_COVER =
            blockTag("loose_rock_cover");

    public static final TagKey<Item> FLINT_SHARDS =
            commonItemTag("flint_shards");
    public static final TagKey<Item> INGOTS_TIN =
            commonItemTag("ingots/tin");
    public static final TagKey<Item> INGOTS_BRONZE =
            commonItemTag("ingots/bronze");
    public static final TagKey<Item> ROCKS =
            commonItemTag("rocks");
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

    private static TagKey<Item> commonItemTag(String path) {
        return TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath("c", path)
        );
    }
}
