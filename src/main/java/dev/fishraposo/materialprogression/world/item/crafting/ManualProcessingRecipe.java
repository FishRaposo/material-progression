package dev.fishraposo.materialprogression.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/** A data-driven operation performed by an installed hand tool. */
public final class ManualProcessingRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<ManualProcessingRecipe> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("tool").forGetter(ManualProcessingRecipe::tool),
                    Ingredient.CODEC.fieldOf("input").forGetter(ManualProcessingRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(ManualProcessingRecipe::result),
                    Codec.INT.fieldOf("durability_cost").forGetter(ManualProcessingRecipe::durabilityCost),
                    Codec.INT.fieldOf("operation_time").forGetter(ManualProcessingRecipe::operationTime)
            ).apply(instance, ManualProcessingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManualProcessingRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(MAP_CODEC.codec());

    private final Ingredient tool;
    private final Ingredient input;
    private final ItemStackTemplate result;
    private final int durabilityCost;
    private final int operationTime;

    public ManualProcessingRecipe(
            Ingredient tool,
            Ingredient input,
            ItemStackTemplate result,
            int durabilityCost,
            int operationTime
    ) {
        this.tool = tool;
        this.input = input;
        this.result = result;
        if (durabilityCost <= 0) {
            throw new IllegalArgumentException("Manual processing durability cost must be positive");
        }
        this.durabilityCost = durabilityCost;
        if (operationTime <= 0) {
            throw new IllegalArgumentException("Manual processing operation time must be positive");
        }
        this.operationTime = operationTime;
    }

    public ManualProcessingRecipe(
            Ingredient tool,
            Ingredient input,
            ItemStack result,
            int durabilityCost,
            int operationTime
    ) {
        this(tool, input, ItemStackTemplate.fromNonEmptyStack(result), durabilityCost, operationTime);
    }

    public Ingredient tool() {
        return tool;
    }

    public Ingredient input() {
        return input;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public int durabilityCost() {
        return durabilityCost;
    }

    public int operationTime() {
        return operationTime;
    }

    public boolean matches(ItemStack tool, ItemStack input) {
        return this.tool.test(tool) && this.input.test(input);
    }

    public ItemStack resultStack() {
        return result.create();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        // Recipe-manager lookups receive no installed tool, so they must not
        // authorize a manual operation. Workshop uses matches(tool, input).
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return resultStack();
    }

    @Override
    public RecipeSerializer<ManualProcessingRecipe> getSerializer() {
        return ModRecipes.MANUAL_PROCESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<ManualProcessingRecipe> getType() {
        return ModRecipes.MANUAL_PROCESSING.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
