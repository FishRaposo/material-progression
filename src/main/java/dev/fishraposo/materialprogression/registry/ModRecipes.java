package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.item.crafting.CrushingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MaterialProgression.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MaterialProgression.MOD_ID);

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

    private ModRecipes() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}
