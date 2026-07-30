package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.item.crafting.CrushingRecipe;
import dev.fishraposo.materialprogression.world.item.crafting.ManualWorkshopRecipe;
import dev.fishraposo.materialprogression.world.item.crafting.RockCobblingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MaterialProgression.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MaterialProgression.MOD_ID);
    private static final DeferredRegister<RecipeBookCategory>
            RECIPE_BOOK_CATEGORIES = DeferredRegister.create(
                    BuiltInRegistries.RECIPE_BOOK_CATEGORY,
                    MaterialProgression.MOD_ID
            );

    public static final DeferredHolder<
            RecipeBookCategory,
            RecipeBookCategory
    > MANUAL_WORKSHOP_CATEGORY = RECIPE_BOOK_CATEGORIES.register(
            "manual_workshop",
            RecipeBookCategory::new
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrushingRecipe>> CRUSHING =
            RECIPE_TYPES.register("crushing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MaterialProgression.MOD_ID + ":crushing";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER =
            RECIPE_SERIALIZERS.register(
                    "crushing",
                    () -> new RecipeSerializer<>(CrushingRecipe.MAP_CODEC, CrushingRecipe.STREAM_CODEC)
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RockCobblingRecipe>>
            ROCK_COBBLING_SERIALIZER = RECIPE_SERIALIZERS.register(
                    "rock_cobbling",
                    () -> new RecipeSerializer<>(
                            RockCobblingRecipe.MAP_CODEC,
                            RockCobblingRecipe.STREAM_CODEC
                    )
            );

    public static final DeferredHolder<
            RecipeType<?>,
            RecipeType<ManualWorkshopRecipe>
    > MANUAL_WORKSHOP = RECIPE_TYPES.register(
            "manual_workshop",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MaterialProgression.MOD_ID + ":manual_workshop";
                }
            }
    );

    public static final DeferredHolder<
            RecipeSerializer<?>,
            RecipeSerializer<ManualWorkshopRecipe>
    > MANUAL_WORKSHOP_SERIALIZER = RECIPE_SERIALIZERS.register(
            "manual_workshop",
            () -> new RecipeSerializer<>(
                    ManualWorkshopRecipe.MAP_CODEC,
                    ManualWorkshopRecipe.STREAM_CODEC
            )
    );

    private ModRecipes() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_BOOK_CATEGORIES.register(modBus);
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}
