package dev.fishraposo.materialprogression.world.item.crafting;

import com.mojang.serialization.MapCodec;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class CrushingRecipe extends AbstractCookingRecipe {
    public static final MapCodec<CrushingRecipe> MAP_CODEC =
            cookingMapCodec(CrushingRecipe::new, 200);
    public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> STREAM_CODEC =
            cookingStreamCodec(CrushingRecipe::new);

    public CrushingRecipe(
            Recipe.CommonInfo commonInfo,
            CookingBookInfo bookInfo,
            Ingredient ingredient,
            ItemStackTemplate result,
            float experience,
            int cookingTime
    ) {
        super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
    }

    @Override
    protected Item furnaceIcon() {
        return ModBlocks.CRUSHER.get().asItem();
    }

    @Override
    public RecipeSerializer<CrushingRecipe> getSerializer() {
        return ModRecipes.CRUSHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<CrushingRecipe> getType() {
        return ModRecipes.CRUSHING.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }
}
