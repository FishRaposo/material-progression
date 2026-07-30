package dev.fishraposo.materialprogression.world.item.crafting;

import com.mojang.serialization.MapCodec;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public final class RockCobblingRecipe extends CustomRecipe {
    public static final MapCodec<RockCobblingRecipe> MAP_CODEC =
            MapCodec.unit(RockCobblingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, RockCobblingRecipe>
            STREAM_CODEC = StreamCodec.unit(new RockCobblingRecipe());

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 4) {
            return false;
        }
        for (ItemStack stack : input.items()) {
            if (!stack.isEmpty() && !stack.is(ModTags.ROCKS)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        StoneFamilyCatalog catalog = StoneFamilyCatalog.get();
        StoneFamilyCatalog.Entry matching = null;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            var family = catalog.byRock(stack);
            if (family.isEmpty()) {
                return new ItemStack(Items.COBBLESTONE);
            }
            if (matching == null) {
                matching = family.orElseThrow();
            } else if (!matching.id().equals(family.orElseThrow().id())) {
                return new ItemStack(Items.COBBLESTONE);
            }
        }
        return matching == null
                ? ItemStack.EMPTY
                : new ItemStack(matching.cobbledBlock());
    }

    @Override
    public RecipeSerializer<RockCobblingRecipe> getSerializer() {
        return ModRecipes.ROCK_COBBLING_SERIALIZER.get();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        Ingredient rocks = Ingredient.of(
                BuiltInRegistries.ITEM.getOrThrow(ModTags.ROCKS)
        );
        return PlacementInfo.create(List.of(rocks, rocks, rocks, rocks));
    }

    @Override
    public List<RecipeDisplay> display() {
        SlotDisplay rocks = new SlotDisplay.TagSlotDisplay(ModTags.ROCKS);
        return List.of(new ShapelessCraftingRecipeDisplay(
                List.of(rocks, rocks, rocks, rocks),
                new SlotDisplay.ItemSlotDisplay(Items.COBBLESTONE),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }
}
