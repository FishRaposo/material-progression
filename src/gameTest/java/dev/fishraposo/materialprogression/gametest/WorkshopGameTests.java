package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.world.inventory.ManualWorkshopMenu;
import dev.fishraposo.materialprogression.world.item.crafting.ManualWorkshopRecipe;
import dev.fishraposo.materialprogression.world.level.block.entity.ManualWorkshopBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class WorkshopGameTests {
    private WorkshopGameTests() {
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Rock sharpening completes on exactly tick 40")
    static void rockSharpeningHasExactTiming(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_KNIFE.get())
                .input(ModItems.ROCK.get());

        workshop.tick(39);
        GameTestSupport.assertEmpty(
                helper,
                workshop.output(),
                "Workshop output before the fortieth tick"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.input(),
                ModItems.ROCK.get(),
                1,
                "Workshop rock before completion"
        );
        helper.assertTrue(
                workshop.tool().getDamageValue() == 0,
                "Workshop damaged its knife before completion"
        );

        workshop.tick(1);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                ModItems.FLINT_SHARD.get(),
                2,
                "Workshop rock-sharpening output"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.input(),
                "Workshop rock after completion"
        );
        helper.assertTrue(
                workshop.tool().getDamageValue() == 1,
                "Workshop rock sharpening did not cost one durability"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop recipes preserve exact 60 and 80 tick processing")
    static void stickAndAggregateRecipesHaveExactTiming(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_SAW.get())
                .input(Items.OAK_PLANKS);

        workshop.tick(59);
        GameTestSupport.assertEmpty(
                helper,
                workshop.output(),
                "Stick output before tick 60"
        );
        workshop.tick(1);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                Items.STICK,
                3,
                "Sawed-stick output"
        );

        workshop.clear();
        workshop.tool(ModItems.FLINT_HAMMER.get())
                .input(Items.STONE);
        workshop.tick(79);
        GameTestSupport.assertEmpty(
                helper,
                workshop.output(),
                "Gravel output before tick 80"
        );
        workshop.tick(1);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                Items.GRAVEL,
                1,
                "Hammered stone output"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Flint and bronze saws share exact 100 tick wood yields")
    static void sawsPreserveWoodSpeciesAndYield(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper);
        List<WoodCase> cases = List.of(
                new WoodCase(
                        ModItems.FLINT_SAW.get(),
                        Items.OAK_LOG,
                        Items.OAK_PLANKS,
                        6
                ),
                new WoodCase(
                        ModItems.BRONZE_SAW.get(),
                        Items.STRIPPED_OAK_WOOD,
                        Items.OAK_PLANKS,
                        6
                ),
                new WoodCase(
                        ModItems.FLINT_SAW.get(),
                        Items.CRIMSON_HYPHAE,
                        Items.CRIMSON_PLANKS,
                        6
                ),
                new WoodCase(
                        ModItems.BRONZE_SAW.get(),
                        Items.STRIPPED_WARPED_STEM,
                        Items.WARPED_PLANKS,
                        6
                ),
                new WoodCase(
                        ModItems.FLINT_SAW.get(),
                        Items.BAMBOO_BLOCK,
                        Items.BAMBOO_PLANKS,
                        3
                )
        );

        for (WoodCase woodCase : cases) {
            workshop.clear();
            workshop.tool(woodCase.tool()).input(woodCase.input());
            workshop.tick(99);
            GameTestSupport.assertEmpty(
                    helper,
                    workshop.output(),
                    "Wood output before tick 100 for " + woodCase.input()
            );
            workshop.tick(1);
            GameTestSupport.assertStack(
                    helper,
                    workshop.output(),
                    woodCase.output(),
                    woodCase.count(),
                    "Wood output for " + woodCase.input()
            );
            helper.assertTrue(
                    workshop.tool().getDamageValue() == 2,
                    "Sawing did not cost two durability"
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Every plant tier produces its literal fiber yield")
    static void plantTiersProduceScaledFiber(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper);
        List<PlantCase> cases = List.of(
                new PlantCase(Items.OAK_LEAVES, 1),
                new PlantCase(Items.POPPY, 1),
                new PlantCase(Items.OAK_SAPLING, 2),
                new PlantCase(Items.SUNFLOWER, 2),
                new PlantCase(Items.WHEAT, 2),
                new PlantCase(Items.CACTUS, 3),
                new PlantCase(Items.SUGAR_CANE, 3),
                new PlantCase(Items.VINE, 5),
                new PlantCase(Items.WEEPING_VINES, 5)
        );

        for (PlantCase plantCase : cases) {
            workshop.clear();
            workshop.tool(ModItems.FLINT_KNIFE.get())
                    .input(plantCase.input());
            workshop.tick(40);
            GameTestSupport.assertStack(
                    helper,
                    workshop.output(),
                    ModItems.PLANT_FIBER.get(),
                    plantCase.count(),
                    "Plant fiber output for " + plantCase.input()
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop manual ore crushing doubles every current input")
    static void oreAndRawMetalRecipesDoubleDust(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper);
        List<ProcessCase> cases = List.of(
                new ProcessCase(
                        Items.COPPER_ORE,
                        ModItems.COPPER_DUST.get()
                ),
                new ProcessCase(
                        Items.RAW_COPPER,
                        ModItems.COPPER_DUST.get()
                ),
                new ProcessCase(
                        ModItems.TIN_ORE.get(),
                        ModItems.TIN_DUST.get()
                ),
                new ProcessCase(
                        ModItems.RAW_TIN.get(),
                        ModItems.TIN_DUST.get()
                )
        );

        for (ProcessCase processCase : cases) {
            workshop.clear();
            workshop.tool(ModItems.BRONZE_HAMMER.get())
                    .input(processCase.input());
            workshop.tick(159);
            GameTestSupport.assertEmpty(
                    helper,
                    workshop.output(),
                    "Dust output before tick 160"
            );
            workshop.tick(1);
            GameTestSupport.assertStack(
                    helper,
                    workshop.output(),
                    processCase.output(),
                    2,
                    "Manual ore-crushing output"
            );
            helper.assertTrue(
                    workshop.tool().getDamageValue() == 8,
                    "Ore crushing did not cost eight durability"
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Only stone and cobblestone become generic gravel")
    static void aggregateRecipesPreserveStoneFamilyBoundary(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_HAMMER.get())
                .input(Items.COBBLESTONE);
        workshop.tick(80);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                Items.GRAVEL,
                1,
                "Cobblestone-to-gravel output"
        );

        workshop.clear();
        workshop.tool(ModItems.FLINT_HAMMER.get())
                .input(Items.GRAVEL);
        workshop.tick(80);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                Items.SAND,
                1,
                "Gravel-to-sand output"
        );

        workshop.clear();
        workshop.tool(ModItems.FLINT_HAMMER.get())
                .input(Items.GRANITE);
        workshop.tick(160);
        GameTestSupport.assertEmpty(
                helper,
                workshop.output(),
                "Granite incorrectly became generic gravel"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.input(),
                Items.GRANITE,
                1,
                "Granite input after an unsupported operation"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Blocked output pauses without consuming or damaging")
    static void blockedOutputPausesAtomically(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_KNIFE.get())
                .input(ModItems.ROCK.get())
                .output(new ItemStack(ModItems.FLINT_SHARD.get(), 63));
        workshop.tick(100);

        helper.assertTrue(
                workshop.progress() == 0,
                "Blocked workshop accumulated progress"
        );
        helper.assertTrue(
                workshop.tool().getDamageValue() == 0,
                "Blocked workshop damaged its tool"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.input(),
                ModItems.ROCK.get(),
                1,
                "Blocked workshop input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                ModItems.FLINT_SHARD.get(),
                63,
                "Blocked workshop output"
        );

        workshop.clearOutput();
        workshop.tick(40);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                ModItems.FLINT_SHARD.get(),
                2,
                "Resumed workshop output"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Changing input or tool identity resets progress")
    static void recipeAndIdentityChangesResetProgress(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_HAMMER.get())
                .input(Items.STONE);
        workshop.tick(40);
        helper.assertTrue(
                workshop.progress() == 40,
                "Workshop did not reach partial progress"
        );

        workshop.input(Items.GRAVEL);
        helper.assertTrue(
                workshop.progress() == 0,
                "Changing the input did not reset progress"
        );
        workshop.tick(79);
        GameTestSupport.assertEmpty(
                helper,
                workshop.output(),
                "Changed recipe completed one tick early"
        );

        workshop.tool(ModItems.BRONZE_HAMMER.get());
        helper.assertTrue(
                workshop.progress() == 0,
                "Changing the tool identity did not reset progress"
        );
        workshop.tick(80);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                Items.SAND,
                1,
                "Changed recipe output"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "The final successful operation may break its tool")
    static void completionAndFinalToolBreakAreAtomic(
            ExtendedGameTestHelper helper
    ) {
        ItemStack knife = ModItems.FLINT_KNIFE.get().getDefaultInstance();
        knife.setDamageValue(knife.getMaxDamage() - 1);
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(knife)
                .input(ModItems.ROCK.get());

        workshop.tick(39);
        GameTestSupport.assertStack(
                helper,
                workshop.input(),
                ModItems.ROCK.get(),
                1,
                "Input before final tool-breaking tick"
        );
        workshop.tick(1);
        GameTestSupport.assertStack(
                helper,
                workshop.output(),
                ModItems.FLINT_SHARD.get(),
                2,
                "Output from final tool-breaking operation"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.input(),
                "Input after final tool-breaking operation"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.tool(),
                "Broken tool after successful operation"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop inventory and progress survive block entity serialization")
    static void inventoryAndProgressPersistThroughCodec(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_KNIFE.get())
                .input(ModItems.ROCK.get());
        workshop.tick(20);
        CompoundTag saved = workshop.entity().saveWithFullMetadata(
                helper.getLevel().registryAccess()
        );
        BlockEntity loaded = BlockEntity.loadStatic(
                workshop.entity().getBlockPos(),
                workshop.entity().getBlockState(),
                saved,
                helper.getLevel().registryAccess()
        );
        helper.assertTrue(
                loaded instanceof ManualWorkshopBlockEntity,
                "Serialized workshop did not load as a workshop"
        );
        ManualWorkshopBlockEntity restored =
                (ManualWorkshopBlockEntity) loaded;
        helper.assertTrue(
                restored.progress() == 20 && restored.maxProgress() == 40,
                "Serialized workshop lost its progress"
        );
        GameTestSupport.assertStack(
                helper,
                restored.getItem(ManualWorkshopBlockEntity.TOOL_SLOT),
                ModItems.FLINT_KNIFE.get(),
                1,
                "Serialized workshop tool"
        );
        GameTestSupport.assertStack(
                helper,
                restored.getItem(ManualWorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Serialized workshop input"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop menu synchronizes progress data")
    static void menuReadsSynchronizedProgress(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_KNIFE.get())
                .input(ModItems.ROCK.get());
        workshop.tick(20);
        Player player = helper.makeMockServerPlayer(GameType.SURVIVAL);
        var created = workshop.entity().createMenu(
                7,
                player.getInventory(),
                player
        );
        helper.assertTrue(
                created instanceof ManualWorkshopMenu,
                "Workshop did not create its registered menu"
        );
        ManualWorkshopMenu menu = (ManualWorkshopMenu) created;
        helper.assertTrue(
                menu.progress() == 20
                        && menu.maxProgress() == 40
                        && menu.progressWidth(24) == 12,
                "Workshop menu data did not match the block entity"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Recipe manager loads the public workshop serializer")
    static void recipeManagerLoadsPublicRecipeType(
            ExtendedGameTestHelper helper
    ) {
        ResourceKey<Recipe<?>> key = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath(
                        MaterialProgression.MOD_ID,
                        "manual_workshop_rock_sharpening"
                )
        );
        RecipeHolder<?> holder = helper.getLevel()
                .recipeAccess()
                .byKey(key)
                .orElseThrow();
        helper.assertTrue(
                holder.value() instanceof ManualWorkshopRecipe,
                "Workshop JSON did not decode through its public serializer"
        );
        ManualWorkshopRecipe recipe =
                (ManualWorkshopRecipe) holder.value();
        helper.assertTrue(
                recipe.processingTime() == 40
                        && recipe.toolDamage() == 1
                        && recipe.result().is(ModItems.FLINT_SHARD.get())
                        && recipe.result().getCount() == 2,
                "Decoded workshop recipe fields were incorrect"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 80)
    @EmptyTemplate(value = "5x5x5", floor = true)
    @TestHolder(description = "Real hoppers cannot insert into or extract from the workshop")
    static void hoppersCannotAutomateWorkshop(
            ExtendedGameTestHelper helper
    ) {
        BlockPos workshopPos = new BlockPos(2, 2, 2);
        WorkshopFixture workshop = WorkshopFixture.placeAt(
                helper,
                workshopPos
        ).tool(ModItems.FLINT_KNIFE.get())
                .output(new ItemStack(ModItems.FLINT_SHARD.get(), 2));
        BlockPos topPos = workshopPos.above();
        BlockPos bottomPos = workshopPos.below();
        helper.setBlock(
                topPos,
                Blocks.HOPPER.defaultBlockState().setValue(
                        HopperBlock.FACING,
                        Direction.DOWN
                )
        );
        helper.setBlock(
                bottomPos,
                Blocks.HOPPER.defaultBlockState().setValue(
                        HopperBlock.FACING,
                        Direction.EAST
                )
        );
        HopperBlockEntity top = helper.getBlockEntity(
                topPos,
                HopperBlockEntity.class
        );
        HopperBlockEntity bottom = helper.getBlockEntity(
                bottomPos,
                HopperBlockEntity.class
        );
        top.setItem(0, ModItems.ROCK.get().getDefaultInstance());

        helper.startSequence()
                .thenIdle(20)
                .thenExecute(() -> {
                    GameTestSupport.assertStack(
                            helper,
                            top.getItem(0),
                            ModItems.ROCK.get(),
                            1,
                            "Hopper item rejected by workshop input"
                    );
                    GameTestSupport.assertEmpty(
                            helper,
                            workshop.input(),
                            "Workshop input after hopper insertion attempt"
                    );
                    GameTestSupport.assertStack(
                            helper,
                            workshop.output(),
                            ModItems.FLINT_SHARD.get(),
                            2,
                            "Workshop output after hopper extraction attempt"
                    );
                    GameTestSupport.assertEmpty(
                            helper,
                            bottom.getItem(0),
                            "Hopper below workshop"
                    );
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Breaking a workshop drops inventory and discards partial progress")
    static void breakingDropsInventoryAndLosesProgress(
            ExtendedGameTestHelper helper
    ) {
        WorkshopFixture workshop = WorkshopFixture.place(helper)
                .tool(ModItems.FLINT_KNIFE.get())
                .input(new ItemStack(ModItems.ROCK.get(), 2))
                .output(new ItemStack(ModItems.FLINT_SHARD.get(), 3));
        workshop.tick(20);
        helper.assertTrue(
                workshop.progress() == 20,
                "Workshop did not have partial progress before breaking"
        );

        helper.breakBlock(
                WorkshopFixture.POSITION,
                Items.IRON_AXE.getDefaultInstance(),
                null
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.MANUAL_WORKSHOP.get(),
                WorkshopFixture.POSITION,
                1.5,
                1
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.FLINT_KNIFE.get(),
                WorkshopFixture.POSITION,
                1.5,
                1
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.ROCK.get(),
                WorkshopFixture.POSITION,
                1.5,
                1
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.FLINT_SHARD.get(),
                WorkshopFixture.POSITION,
                1.5,
                1
        );

        WorkshopFixture replacement = WorkshopFixture.place(helper);
        helper.assertTrue(
                replacement.progress() == 0
                        && replacement.entity().maxProgress() == 0,
                "Re-placed workshop retained partial progress"
        );
        helper.succeed();
    }

    private record WoodCase(
            Item tool,
            Item input,
            Item output,
            int count
    ) {
    }

    private record PlantCase(Item input, int count) {
    }

    private record ProcessCase(Item input, Item output) {
    }
}
