package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.progression.FeedbackMessages;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.GeologyTierResolver;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class DiscoverabilityGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(2, 2, 2);

    private DiscoverabilityGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Opening items carry localized role tooltips")
    static void itemLoreUsesStableTranslationKeys(
            ExtendedGameTestHelper helper
    ) {
        assertLore(
                helper,
                ModItems.ROCK.get().getDefaultInstance(),
                "tooltip.material_progression.rock"
        );
        assertLore(
                helper,
                ModItems.CINNABAR_ROCK.get().getDefaultInstance(),
                "tooltip.material_progression.rock"
        );
        assertLore(
                helper,
                ModItems.FLINT_SHARD.get().getDefaultInstance(),
                "tooltip.material_progression.flint_shard"
        );
        assertLore(
                helper,
                ModItems.PLANT_FIBER.get().getDefaultInstance(),
                "tooltip.material_progression.plant_fiber"
        );
        assertLore(
                helper,
                ModItems.FLINT_HATCHET.get().getDefaultInstance(),
                "tooltip.material_progression.flint_hatchet"
        );
        assertLore(
                helper,
                ModItems.FLINT_KNIFE.get().getDefaultInstance(),
                "tooltip.material_progression.knife"
        );
        assertLore(
                helper,
                ModItems.BRONZE_HAMMER.get().getDefaultInstance(),
                "tooltip.material_progression.hammer"
        );
        assertLore(
                helper,
                ModItems.BRONZE_SAW.get().getDefaultInstance(),
                "tooltip.material_progression.saw"
        );
        assertLore(
                helper,
                ModItems.MANUAL_WORKSHOP.get().getDefaultInstance(),
                "tooltip.material_progression.manual_workshop"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Feedback components retain keys and parameters")
    static void feedbackUsesTierFamilyAndCapabilityParameters(
            ExtendedGameTestHelper helper
    ) {
        Component geology = FeedbackMessages.insufficientGeology(
                StoneFamilyCatalog.get()
                        .byFamily(StoneFamily.CINNABAR)
                        .orElseThrow(),
                GeologyTier.LEVEL_2
        );
        TranslatableContents outer = translatable(
                helper,
                geology,
                "geology feedback"
        );
        helper.assertValueEqual(
                "message.material_progression.geology.insufficient",
                outer.getKey(),
                "geology feedback key"
        );
        helper.assertValueEqual(
                3,
                outer.getArgs().length,
                "geology feedback parameter count"
        );
        assertTranslationArgument(
                helper,
                outer.getArgs()[0],
                "message.material_progression.geology.tier.2",
                "tier argument"
        );
        assertTranslationArgument(
                helper,
                outer.getArgs()[1],
                "stone_family.material_progression.cinnabar",
                "family argument"
        );
        assertTranslationArgument(
                helper,
                outer.getArgs()[2],
                "message.material_progression.geology.capability.2",
                "capability argument"
        );

        TranslatableContents correctTool = translatable(
                helper,
                FeedbackMessages.correctToolRequired(
                        StoneFamilyCatalog.get()
                                .byFamily(StoneFamily.STONE)
                                .orElseThrow()
                ),
                "correct-tool feedback"
        );
        helper.assertValueEqual(
                "message.material_progression.geology.correct_tool",
                correctTool.getKey(),
                "correct-tool feedback key"
        );
        helper.assertValueEqual(
                1,
                correctTool.getArgs().length,
                "correct-tool feedback parameter count"
        );
        assertTranslationArgument(
                helper,
                correctTool.getArgs()[0],
                "stone_family.material_progression.stone",
                "correct-tool family argument"
        );

        TranslatableContents log = translatable(
                helper,
                FeedbackMessages.logRequiresTool(),
                "log feedback"
        );
        helper.assertValueEqual(
                "message.material_progression.log.requires_tool",
                log.getKey(),
                "log feedback key"
        );
        helper.assertValueEqual(
                0,
                log.getArgs().length,
                "log feedback parameter count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Log feedback is throttled and skips valid paths")
    static void logFeedbackThrottleHonorsToolConfigAndCreative(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.OAK_LOG);
        ServerPlayer first = player(helper, GameType.SURVIVAL, ItemStack.EMPTY);
        ServerPlayer second = player(
                helper,
                GameType.SURVIVAL,
                ItemStack.EMPTY
        );
        long now = helper.getLevel().getGameTime();

        first.setData(ModDataAttachments.LOG_FEEDBACK_TICK, now - 19);
        postStart(helper, first);
        helper.assertValueEqual(
                now - 19,
                first.getData(ModDataAttachments.LOG_FEEDBACK_TICK),
                "throttled log feedback tick"
        );

        postStart(helper, second);
        helper.assertValueEqual(
                now,
                second.getData(ModDataAttachments.LOG_FEEDBACK_TICK),
                "independent log feedback tick"
        );

        first.setData(ModDataAttachments.LOG_FEEDBACK_TICK, now - 20);
        first.setItemInHand(
                InteractionHand.MAIN_HAND,
                ModItems.FLINT_SAW.get().getDefaultInstance()
        );
        postStart(helper, first);
        helper.assertValueEqual(
                now - 20,
                first.getData(ModDataAttachments.LOG_FEEDBACK_TICK),
                "valid Saw path feedback tick"
        );

        first.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        ConfigFixture.setRequireAxeForLogs(helper, false);
        postStart(helper, first);
        helper.assertValueEqual(
                now - 20,
                first.getData(ModDataAttachments.LOG_FEEDBACK_TICK),
                "config opt-out feedback tick"
        );

        ConfigFixture.setRequireAxeForLogs(helper, true);
        ServerPlayer creative = player(
                helper,
                GameType.CREATIVE,
                ItemStack.EMPTY
        );
        postStart(helper, creative);
        helper.assertValueEqual(
                -20L,
                creative.getData(ModDataAttachments.LOG_FEEDBACK_TICK),
                "creative feedback tick"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A real insufficient Dense interaction advances")
    static void denseEncounterRequiresInsufficientDenseInteraction(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        encloseCinnabar(helper);
        var tier = GeologyTierResolver.resolve(
                helper.getLevel(),
                helper.absolutePos(BLOCK_POS),
                Blocks.CINNABAR.defaultBlockState()
        );
        helper.assertValueEqual(
                tier.orElseThrow(),
                GeologyTier.LEVEL_2,
                "Dense Cinnabar fixture tier"
        );

        var advancement = helper.getLevel()
                .getServer()
                .getAdvancements()
                .get(Identifier.fromNamespaceAndPath(
                        MaterialProgression.MOD_ID,
                        "progression/dense_geology"
                ));
        helper.assertTrue(
                advancement != null,
                "Dense geology advancement was not loaded"
        );

        ServerPlayer insufficient = player(
                helper,
                GameType.SURVIVAL,
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        postStart(helper, insufficient);
        helper.assertTrue(
                insufficient.getAdvancements()
                        .getOrStartProgress(advancement)
                        .isDone(),
                "insufficient Dense interaction did not award advancement"
        );

        ServerPlayer capable = player(
                helper,
                GameType.SURVIVAL,
                new ItemStack(Items.IRON_PICKAXE)
        );
        postStart(helper, capable);
        helper.assertFalse(
                capable.getAdvancements()
                        .getOrStartProgress(advancement)
                        .isDone(),
                "capable Dense interaction unexpectedly awarded advancement"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Workshop recipes use their own book category")
    static void workshopRecipeCategoryIsNotCraftingMisc(
            ExtendedGameTestHelper helper
    ) {
        helper.assertValueEqual(
                ModRecipes.MANUAL_WORKSHOP_CATEGORY.get(),
                helper.getLevel()
                        .getServer()
                        .getRecipeManager()
                        .getRecipes()
                        .stream()
                        .filter(holder -> holder.value().getType()
                                == ModRecipes.MANUAL_WORKSHOP.get())
                        .findFirst()
                        .orElseThrow()
                        .value()
                        .recipeBookCategory(),
                "Manual Workshop recipe-book category"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 20)
    @EmptyTemplate
    @TestHolder(description = "Flint discovery unlocks only its shard recipe")
    static void flintInventoryUnlocksOnlyFlintShardRecipe(
            ExtendedGameTestHelper helper
    ) {
        ServerPlayer player = player(
                helper,
                GameType.SURVIVAL,
                ItemStack.EMPTY
        );
        ResourceKey<Recipe<?>> flintShardRecipe = recipeKey(
                "flint_shard_from_flint"
        );
        ResourceKey<Recipe<?>> rockShardRecipe = recipeKey(
                "flint_shard_from_rock"
        );
        helper.assertFalse(
                player.getRecipeBook().contains(flintShardRecipe),
                "Flint shard recipe was already known"
        );
        helper.assertFalse(
                player.getRecipeBook().contains(rockShardRecipe),
                "Rock shard recipe was already known"
        );

        ItemStack acquiredFlint = new ItemStack(Items.FLINT);
        player.getInventory().add(acquiredFlint);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(
                player,
                player.getInventory(),
                acquiredFlint
        );

        helper.succeedWhen(() -> {
            helper.assertTrue(
                    player.getRecipeBook().contains(flintShardRecipe),
                    "Flint inventory did not unlock its shard recipe"
            );
            helper.assertFalse(
                    player.getRecipeBook().contains(rockShardRecipe),
                    "Flint inventory unlocked the Rock sharpening recipe"
            );
        });
    }

    private static void assertLore(
            ExtendedGameTestHelper helper,
            ItemStack stack,
            String expectedKey
    ) {
        ItemLore lore = stack.get(DataComponents.LORE);
        helper.assertTrue(
                lore != null,
                stack.getItem() + " has no Lore component"
        );
        helper.assertValueEqual(
                1,
                lore.lines().size(),
                stack.getItem() + " lore line count"
        );
        helper.assertValueEqual(
                expectedKey,
                translatable(helper, lore.lines().getFirst(), "item lore")
                        .getKey(),
                stack.getItem() + " lore key"
        );
    }

    private static void assertTranslationArgument(
            ExtendedGameTestHelper helper,
            Object value,
            String expectedKey,
            String label
    ) {
        helper.assertTrue(value instanceof Component, label + " is not a Component");
        helper.assertValueEqual(
                expectedKey,
                translatable(helper, (Component) value, label).getKey(),
                label + " key"
        );
    }

    private static TranslatableContents translatable(
            ExtendedGameTestHelper helper,
            Component component,
            String label
    ) {
        helper.assertTrue(
                component.getContents() instanceof TranslatableContents,
                label + " is not translatable"
        );
        return (TranslatableContents) component.getContents();
    }

    private static ServerPlayer player(
            ExtendedGameTestHelper helper,
            GameType gameType,
            ItemStack held
    ) {
        ServerPlayer player = helper.makeTickingMockServerPlayerInLevel(
                gameType
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        return player;
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath(
                        MaterialProgression.MOD_ID,
                        path
                )
        );
    }

    private static void postStart(
            ExtendedGameTestHelper helper,
            ServerPlayer player
    ) {
        NeoForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickBlock(
                player,
                helper.absolutePos(BLOCK_POS),
                Direction.UP,
                PlayerInteractEvent.LeftClickBlock.Action.START
        ));
    }

    private static void encloseCinnabar(ExtendedGameTestHelper helper) {
        helper.setBlock(BLOCK_POS, Blocks.CINNABAR);
        for (Direction direction : Direction.values()) {
            helper.setBlock(BLOCK_POS.relative(direction), Blocks.STONE);
        }
        helper.setBlock(BLOCK_POS.above(), Blocks.AIR);
    }
}
