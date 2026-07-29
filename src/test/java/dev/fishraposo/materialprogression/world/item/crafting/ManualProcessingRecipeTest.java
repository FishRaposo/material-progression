package dev.fishraposo.materialprogression.world.item.crafting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.junit.jupiter.api.Test;

class ManualProcessingRecipeTest {
    @Test
    void matchesOnlyTheDeclaredToolAndInputIngredients() {
        ManualProcessingRecipe recipe = new ManualProcessingRecipe(
                Ingredient.of(Items.SHEARS),
                Ingredient.of(Items.OAK_LEAVES),
                new ItemStack(Items.STRING, 2),
                1,
                20
        );

        assertTrue(recipe.matches(
                new ItemStack(Items.SHEARS),
                new ItemStack(Items.OAK_LEAVES)
        ));
        assertFalse(recipe.matches(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.OAK_LEAVES)
        ));
        assertFalse(recipe.matches(
                new ItemStack(Items.SHEARS),
                new ItemStack(Items.OAK_LOG)
        ));
    }

    @Test
    void exposesOnlyTheMaterialInputForRecipePlacement() {
        ManualProcessingRecipe recipe = new ManualProcessingRecipe(
                Ingredient.of(Items.SHEARS),
                Ingredient.of(Items.OAK_LEAVES),
                new ItemStack(Items.STRING, 2),
                1,
                20
        );

        var placementInfo = recipe.placementInfo();

        assertEquals(1, placementInfo.ingredients().size());
        assertTrue(placementInfo.ingredients().getFirst().test(new ItemStack(Items.OAK_LEAVES)));
        assertFalse(placementInfo.ingredients().getFirst().test(new ItemStack(Items.SHEARS)));
        assertEquals(1, placementInfo.slotsToIngredientIndex().size());
        assertEquals(0, placementInfo.slotsToIngredientIndex().getInt(0));
        assertEquals("", recipe.group());
        assertFalse(recipe.showNotification());
        assertTrue(recipe.display().isEmpty());
    }

    @Test
    void matchesItemsThroughTagIngredients() {
        ManualProcessingRecipe recipe = new ManualProcessingRecipe(
                Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(TagKey.create(
                        Registries.ITEM,
                        Identifier.fromNamespaceAndPath("minecraft", "swords")
                ))),
                Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(TagKey.create(
                        Registries.ITEM,
                        Identifier.fromNamespaceAndPath("minecraft", "logs")
                ))),
                new ItemStack(Items.STICK),
                1,
                20
        );

        assertTrue(recipe.matches(
                new ItemStack(Items.WOODEN_SWORD),
                new ItemStack(Items.OAK_LOG)
        ));
    }

    @Test
    void mapCodecRoundTripPreservesProcessingBehavior() {
        ManualProcessingRecipe original = new ManualProcessingRecipe(
                Ingredient.of(Items.SHEARS),
                Ingredient.of(Items.OAK_LEAVES),
                new ItemStack(Items.STRING, 2),
                1,
                20
        );
        ManualProcessingRecipe decoded = ManualProcessingRecipe.MAP_CODEC.codec()
                .parse(
                        JsonOps.INSTANCE,
                        ManualProcessingRecipe.MAP_CODEC.codec()
                                .encodeStart(JsonOps.INSTANCE, original)
                                .getOrThrow()
                )
                .getOrThrow();

        assertTrue(decoded.matches(
                new ItemStack(Items.SHEARS),
                new ItemStack(Items.OAK_LEAVES)
        ));
        assertTrue(decoded.resultStack().is(Items.STRING));
        assertTrue(decoded.resultStack().getCount() == 2);
    }

    @Test
    void genericRecipeLookupCannotAuthorizeAnOperationWithoutAnInstalledTool() {
        ManualProcessingRecipe recipe = new ManualProcessingRecipe(
                Ingredient.of(Items.SHEARS),
                Ingredient.of(Items.OAK_LEAVES),
                new ItemStack(Items.STRING, 2),
                1,
                20
        );

        assertFalse(recipe.matches(
                new SingleRecipeInput(new ItemStack(Items.OAK_LEAVES)),
                null
        ));
    }

    @Test
    void streamCodecRoundTripPreservesProcessingBehaviorWithBuiltInRegistryContext() {
        ManualProcessingRecipe original = new ManualProcessingRecipe(
                Ingredient.of(Items.SHEARS),
                Ingredient.of(Items.OAK_LEAVES),
                new ItemStack(Items.STRING, 2),
                1,
                20
        );
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY),
                ConnectionType.NEOFORGE
        );
        try {
            ManualProcessingRecipe.STREAM_CODEC.encode(buffer, original);
            ManualProcessingRecipe decoded = ManualProcessingRecipe.STREAM_CODEC.decode(buffer);

            assertTrue(decoded.matches(
                    new ItemStack(Items.SHEARS),
                    new ItemStack(Items.OAK_LEAVES)
            ));
            assertEquals(1, decoded.durabilityCost());
            assertEquals(20, decoded.operationTime());
            assertEquals(2, decoded.resultStack().getCount());
        } finally {
            buffer.release();
        }
    }

    @Test
    void exposesManualProcessingTypeAndSerializerUnderTheProductionIds() {
        Identifier expected = Identifier.fromNamespaceAndPath(
                "material_progression", "manual_processing"
        );

        assertEquals(expected, ModRecipes.MANUAL_PROCESSING.getId());
        assertEquals(expected, ModRecipes.MANUAL_PROCESSING_SERIALIZER.getId());
    }
}
