package dev.fishraposo.materialprogression.gametest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class WorkshopRecipeReloadFixture implements AutoCloseable {
    private static final Method APPLY_METHOD = findApplyMethod();

    private final RecipeManager manager;
    private final ResourceManager resources;
    private final RecipeMap originalMap;
    private final List<RecipeHolder<?>> originalRecipes;

    private WorkshopRecipeReloadFixture(
            RecipeManager manager,
            ResourceManager resources
    ) {
        this.manager = manager;
        this.resources = resources;
        this.originalMap = manager.recipeMap();
        this.originalRecipes = List.copyOf(manager.getRecipes());
    }

    static WorkshopRecipeReloadFixture capture(
            ExtendedGameTestHelper helper
    ) {
        return new WorkshopRecipeReloadFixture(
                helper.getLevel().getServer().getRecipeManager(),
                helper.getLevel().getServer().getResourceManager()
        );
    }

    void replace(
            ResourceKey<Recipe<?>> key,
            Recipe<?> replacement
    ) {
        List<RecipeHolder<?>> recipes = new ArrayList<>(
                originalRecipes.size()
        );
        boolean replaced = false;
        for (RecipeHolder<?> holder : originalRecipes) {
            if (holder.id().equals(key)) {
                recipes.add(new RecipeHolder<>(key, replacement));
                replaced = true;
            } else {
                recipes.add(holder);
            }
        }
        if (!replaced) {
            throw new IllegalArgumentException(
                    "Recipe does not exist: " + key.identifier()
            );
        }
        apply(RecipeMap.create(recipes));
    }

    void remove(ResourceKey<Recipe<?>> key) {
        List<RecipeHolder<?>> recipes = originalRecipes.stream()
                .filter(holder -> !holder.id().equals(key))
                .toList();
        if (recipes.size() == originalRecipes.size()) {
            throw new IllegalArgumentException(
                    "Recipe does not exist: " + key.identifier()
            );
        }
        apply(RecipeMap.create(recipes));
    }

    @Override
    public void close() {
        apply(originalMap);
    }

    private void apply(RecipeMap recipes) {
        try {
            APPLY_METHOD.invoke(
                    manager,
                    recipes,
                    resources,
                    InactiveProfiler.INSTANCE
            );
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException(
                    "Could not access RecipeManager reload application",
                    exception
            );
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException(
                    "RecipeManager reload application failed",
                    exception.getCause()
            );
        }
    }

    private static Method findApplyMethod() {
        try {
            Method method = RecipeManager.class.getDeclaredMethod(
                    "apply",
                    RecipeMap.class,
                    ResourceManager.class,
                    ProfilerFiller.class
            );
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
