package dev.fishraposo.materialprogression.planner;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.junit.jupiter.api.Test;

class RecipeApiInspectionTest {
    @Test
    void printTargetRecipeApi() {
        inspect(PlacementInfo.class);
        inspect(Ingredient.class);
        inspect(Recipe.class);
        inspect(CraftingRecipe.class);
        inspect(CraftingInput.class);
        inspect(RecipeDisplay.class);
        inspect(SlotDisplay.class);
        fail("Intentional one-run target API inspection");
    }

    private static void inspect(Class<?> type) {
        System.out.println("INSPECT " + type.getName());
        Arrays.stream(type.getDeclaredMethods())
                .map(Method::toGenericString)
                .sorted()
                .forEach(System.out::println);
        Arrays.stream(type.getDeclaredClasses())
                .map(Class::getName)
                .sorted()
                .forEach(name -> System.out.println("NESTED " + name));
    }
}
