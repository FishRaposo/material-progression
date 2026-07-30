package dev.fishraposo.materialprogression.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public final class ManualWorkshopRecipe
        implements Recipe<ManualWorkshopRecipeInput> {
    private static final Codec<Integer> POSITIVE_INT =
            Codec.intRange(1, Integer.MAX_VALUE);

    public static final MapCodec<ManualWorkshopRecipe> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(
                            recipe -> recipe.commonInfo
                    ),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(
                            recipe -> recipe.ingredient
                    ),
                    Ingredient.CODEC.fieldOf("tool").forGetter(
                            recipe -> recipe.tool
                    ),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(
                            recipe -> recipe.result
                    ),
                    POSITIVE_INT.fieldOf("processing_time").forGetter(
                            recipe -> recipe.processingTime
                    ),
                    POSITIVE_INT.fieldOf("tool_damage").forGetter(
                            recipe -> recipe.toolDamage
                    )
            ).apply(instance, ManualWorkshopRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ManualWorkshopRecipe>
            STREAM_CODEC = StreamCodec.composite(
                    Recipe.CommonInfo.STREAM_CODEC,
                    recipe -> recipe.commonInfo,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    recipe -> recipe.ingredient,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    recipe -> recipe.tool,
                    ItemStackTemplate.STREAM_CODEC,
                    recipe -> recipe.result,
                    ByteBufCodecs.VAR_INT,
                    recipe -> recipe.processingTime,
                    ByteBufCodecs.VAR_INT,
                    recipe -> recipe.toolDamage,
                    ManualWorkshopRecipe::new
            );

    private final Recipe.CommonInfo commonInfo;
    private final Ingredient ingredient;
    private final Ingredient tool;
    private final ItemStackTemplate result;
    private final int processingTime;
    private final int toolDamage;

    public ManualWorkshopRecipe(
            Recipe.CommonInfo commonInfo,
            Ingredient ingredient,
            Ingredient tool,
            ItemStackTemplate result,
            int processingTime,
            int toolDamage
    ) {
        this.commonInfo = commonInfo;
        this.ingredient = ingredient;
        this.tool = tool;
        this.result = result;
        this.processingTime = processingTime;
        this.toolDamage = toolDamage;
    }

    @Override
    public boolean matches(ManualWorkshopRecipeInput input, Level level) {
        return tool.test(input.tool())
                && ingredient.test(input.ingredient());
    }

    @Override
    public ItemStack assemble(ManualWorkshopRecipeInput input) {
        return result.create();
    }

    @Override
    public boolean showNotification() {
        return commonInfo.showNotification();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<ManualWorkshopRecipe> getSerializer() {
        return ModRecipes.MANUAL_WORKSHOP_SERIALIZER.get();
    }

    @Override
    public RecipeType<ManualWorkshopRecipe> getType() {
        return ModRecipes.MANUAL_WORKSHOP.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(tool, ingredient));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(
                List.of(tool.display(), ingredient.display()),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(
                        ModBlocks.MANUAL_WORKSHOP.get().asItem()
                )
        ));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public Ingredient tool() {
        return tool;
    }

    public ItemStack result() {
        return result.create();
    }

    public int processingTime() {
        return processingTime;
    }

    public int toolDamage() {
        return toolDamage;
    }
}
