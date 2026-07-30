package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import dev.fishraposo.materialprogression.stone.StoneResistance;
import dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class StoneFamilyGameTests {
    private static final BlockPos ROOT = new BlockPos(1, 1, 1);

    private StoneFamilyGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Configured worldgen places every required family")
    static void configuredFeaturePlacesEveryRequiredFamily(
            ExtendedGameTestHelper helper
    ) {
        List<PlacementCase> cases = List.of(
                new PlacementCase("Overworld Stone", Blocks.STONE, StoneFamily.STONE),
                new PlacementCase("Overworld Granite", Blocks.GRANITE, StoneFamily.GRANITE),
                new PlacementCase("Overworld Diorite", Blocks.DIORITE, StoneFamily.DIORITE),
                new PlacementCase("Overworld Andesite", Blocks.ANDESITE, StoneFamily.ANDESITE),
                new PlacementCase("cave Deepslate", Blocks.DEEPSLATE, StoneFamily.DEEPSLATE),
                new PlacementCase("cave Tuff", Blocks.TUFF, StoneFamily.TUFF),
                new PlacementCase("cave Calcite", Blocks.CALCITE, StoneFamily.CALCITE),
                new PlacementCase(
                        "cave Dripstone",
                        Blocks.DRIPSTONE_BLOCK,
                        StoneFamily.DRIPSTONE
                ),
                new PlacementCase("cave Sulfur", Blocks.SULFUR, StoneFamily.SULFUR),
                new PlacementCase("cave Cinnabar", Blocks.CINNABAR, StoneFamily.CINNABAR),
                new PlacementCase(
                        "raw Sandstone",
                        Blocks.SANDSTONE,
                        StoneFamily.SANDSTONE
                ),
                new PlacementCase(
                        "raw Red Sandstone",
                        Blocks.RED_SANDSTONE,
                        StoneFamily.RED_SANDSTONE
                ),
                new PlacementCase(
                        "Nether Netherrack",
                        Blocks.NETHERRACK,
                        StoneFamily.NETHERRACK
                ),
                new PlacementCase("Nether Basalt", Blocks.BASALT, StoneFamily.BASALT),
                new PlacementCase(
                        "Nether Blackstone",
                        Blocks.BLACKSTONE,
                        StoneFamily.BLACKSTONE
                ),
                new PlacementCase(
                        "End End Stone",
                        Blocks.END_STONE,
                        StoneFamily.END_STONE
                ),
                new PlacementCase(
                        "Sand direct surface",
                        Blocks.SAND,
                        StoneFamily.SANDSTONE
                ),
                new PlacementCase(
                        "Red Sand direct surface",
                        Blocks.RED_SAND,
                        StoneFamily.RED_SANDSTONE
                )
        );
        for (PlacementCase placement : cases) {
            helper.setBlock(ROOT, placement.support());
            helper.setBlock(ROOT.above(), Blocks.AIR);
            assertConfiguredPlacement(
                    helper,
                    ROOT.above(),
                    placement.expected(),
                    placement.label()
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Configured worldgen scans Dirt and Gravel cover")
    static void configuredFeatureUsesNearestFamilyBelowCover(
            ExtendedGameTestHelper helper
    ) {
        List<CoverPlacementCase> cases = List.of(
                new CoverPlacementCase(
                        "Dirt over Granite",
                        Blocks.DIRT,
                        Blocks.GRANITE,
                        StoneFamily.GRANITE
                ),
                new CoverPlacementCase(
                        "Gravel over Tuff",
                        Blocks.GRAVEL,
                        Blocks.TUFF,
                        StoneFamily.TUFF
                )
        );
        for (CoverPlacementCase placement : cases) {
            helper.setBlock(ROOT, Blocks.STONE);
            helper.setBlock(ROOT.above(), placement.nearestSource());
            helper.setBlock(ROOT.above(2), placement.cover());
            helper.setBlock(ROOT.above(3), Blocks.AIR);
            assertConfiguredPlacement(
                    helper,
                    ROOT.above(3),
                    placement.expected(),
                    placement.label()
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Stone-family catalog lookups stay identity-safe")
    static void catalogLookupsHaveNoFallback(ExtendedGameTestHelper helper) {
        var catalog = StoneFamilyCatalog.get();
        var granite = catalog.byFamily(StoneFamily.GRANITE).orElseThrow();
        helper.assertValueEqual(
                granite,
                catalog.bySource(Blocks.GRANITE.defaultBlockState()).orElseThrow(),
                "source lookup"
        );
        helper.assertValueEqual(
                granite,
                catalog.byRock(new ItemStack(ModItems.GRANITE_ROCK.get())).orElseThrow(),
                "Rock lookup"
        );
        helper.assertValueEqual(
                granite,
                catalog.byId(StoneFamily.GRANITE.id()).orElseThrow(),
                "family ID lookup"
        );
        helper.assertValueEqual(
                granite,
                catalog.byRaw(Blocks.GRANITE).orElseThrow(),
                "raw block lookup"
        );
        helper.assertValueEqual(
                granite,
                catalog.byCobble(ModBlocks.COBBLED_GRANITE.get()).orElseThrow(),
                "cobbled block lookup"
        );
        helper.assertValueEqual(
                StoneResistance.STANDARD,
                granite.resistance().tier(),
                "resistance tier"
        );
        helper.assertValueEqual(
                1.0F,
                granite.resistance().modifier(),
                "resistance modifier"
        );
        helper.assertTrue(
                catalog.bySource(Blocks.DIRT.defaultBlockState()).isEmpty()
                        && catalog.byRock(new ItemStack(Items.STICK)).isEmpty()
                        && catalog.byId(Identifier.parse("material_progression:unknown")).isEmpty()
                        && catalog.byIndex(-1).isEmpty(),
                "unknown catalog values fell back to a family"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Cover scan selects the nearest raw family")
    static void coverScanUsesNearestFamily(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.STONE);
        helper.setBlock(ROOT.above(), Blocks.GRANITE);
        helper.setBlock(ROOT.above(2), Blocks.DIRT);
        assertResolvedSupport(helper, ROOT.above(2), StoneFamily.GRANITE);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Cover without a raw family rejects loose-rock placement")
    static void coverWithoutFamilyRejectsPlacement(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT.above(2), Blocks.DIRT);
        Optional<?> resolved = StoneFamilyResolver.resolveSupport(
                helper.getLevel(),
                helper.absolutePos(ROOT.above(2))
        );
        helper.assertTrue(resolved.isEmpty(), "cover unexpectedly resolved a family");
        var looseRocks = ModBlocks.LOOSE_ROCKS.get()
                .defaultBlockState()
                .setValue(LooseRocksBlock.FAMILY, StoneFamily.STONE);
        helper.assertFalse(
                looseRocks.canSurvive(
                        helper.getLevel(),
                        helper.absolutePos(ROOT.above(3))
                ),
                "unresolved cover accepted loose-rock survival"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Configured worldgen resolves soul cover only to Netherrack")
    static void configuredFeatureSoulCoverResolvesNetherrack(
            ExtendedGameTestHelper helper
    ) {
        BlockPos soulSand = ROOT.above(2);
        helper.setBlock(soulSand.below(), Blocks.NETHERRACK);
        helper.setBlock(soulSand, Blocks.SOUL_SAND);
        helper.setBlock(soulSand.above(), Blocks.AIR);
        assertConfiguredPlacement(
                helper,
                soulSand.above(),
                StoneFamily.NETHERRACK,
                "Soul Sand over Netherrack"
        );

        BlockPos soulSoil = ROOT.above(6);
        helper.setBlock(soulSoil.below(), Blocks.BASALT);
        helper.setBlock(soulSoil.below(2), Blocks.BLACKSTONE);
        helper.setBlock(soulSoil.below(3), Blocks.NETHERRACK);
        helper.setBlock(soulSoil.below(4), Blocks.NETHERRACK);
        helper.setBlock(soulSoil, Blocks.SOUL_SOIL);
        helper.setBlock(soulSoil.above(), Blocks.AIR);
        assertConfiguredPlacement(
                helper,
                soulSoil.above(),
                StoneFamily.NETHERRACK,
                "Soul Soil scans past non-Netherrack to nearest Netherrack"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Configured worldgen rejects soul cover over non-Netherrack")
    static void configuredFeatureSoulCoverRejectsOtherFamilies(
            ExtendedGameTestHelper helper
    ) {
        BlockPos soulSand = ROOT.above(3);
        helper.setBlock(soulSand.below(), Blocks.BASALT);
        helper.setBlock(soulSand.below(2), Blocks.BASALT);
        helper.setBlock(soulSand, Blocks.SOUL_SAND);
        helper.setBlock(soulSand.above(), Blocks.AIR);
        helper.assertFalse(
                placeFeature(helper, soulSand.above()),
                "Soul Sand resolved Basalt within the cover scan"
        );
        helper.assertBlockPresent(Blocks.AIR, soulSand.above());

        BlockPos soulSoil = ROOT.above(7);
        helper.setBlock(soulSoil.below(), Blocks.BLACKSTONE);
        helper.setBlock(soulSoil.below(2), Blocks.BLACKSTONE);
        helper.setBlock(soulSoil, Blocks.SOUL_SOIL);
        helper.setBlock(soulSoil.above(), Blocks.AIR);
        helper.assertFalse(
                placeFeature(helper, soulSoil.above()),
                "Soul Soil resolved Blackstone within the cover scan"
        );
        helper.assertBlockPresent(Blocks.AIR, soulSoil.above());
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose rocks drop exactly one Rock from their family")
    static void familyRockDropIsExact(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(
                ROOT.above(),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );
        helper.breakBlock(ROOT.above(), ItemStack.EMPTY, helper.makeMockPlayer());
        assertExactDrop(helper, ROOT.above(), ModItems.GRANITE_ROCK.get(), 1);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose rocks break when support changes to another family")
    static void invalidFamilySupportBreaksLooseRocks(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(
                ROOT.above(),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );
        helper.setBlock(ROOT, Blocks.STONE);
        helper.assertBlockPresent(Blocks.AIR, ROOT.above());
        helper.succeed();
    }

    @GameTest(timeoutTicks = 60)
    @EmptyTemplate
    @TestHolder(description = "Player-breaking a covered source invalidates loose rocks")
    static void playerBreakInvalidatesCoveredLooseRocks(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(ROOT.above(), Blocks.DIRT);
        helper.setBlock(
                ROOT.above(2),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );

        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.CREATIVE
        );
        helper.assertTrue(
                player.gameMode.destroyBlock(helper.absolutePos(ROOT)),
                "server player failed to break the covered source"
        );
        helper.succeedWhen(() ->
                helper.assertBlockPresent(Blocks.AIR, ROOT.above(2))
        );
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Valid loose rocks do not schedule polling ticks")
    static void validLooseRocksDoNotPoll(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(
                ROOT.above(),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );

        helper.assertFalse(
                helper.getLevel().getBlockTicks().hasScheduledTick(
                        helper.absolutePos(ROOT.above()),
                        ModBlocks.LOOSE_ROCKS.get()
                ),
                "valid loose rocks scheduled a recurring validation tick"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 60)
    @EmptyTemplate
    @TestHolder(description = "Player placement invalidates a changed covered family")
    static void playerPlaceInvalidatesCoveredLooseRocks(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, Blocks.STONE);
        helper.setBlock(ROOT.above(2), Blocks.DIRT);
        helper.setBlock(
                ROOT.above(3),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.STONE)
        );

        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.CREATIVE
        );
        ItemStack granite = new ItemStack(Blocks.GRANITE);
        player.setItemInHand(InteractionHand.MAIN_HAND, granite);
        helper.placeAt(player, granite, ROOT, Direction.UP);
        helper.assertBlockPresent(Blocks.GRANITE, ROOT.above());
        helper.succeedWhen(() ->
                helper.assertBlockPresent(Blocks.AIR, ROOT.above(3))
        );
    }

    @GameTest(timeoutTicks = 80)
    @EmptyTemplate
    @TestHolder(description = "Piston movement invalidates loose rocks above the moved source")
    static void pistonMoveInvalidatesCoveredLooseRocks(
            ExtendedGameTestHelper helper
    ) {
        BlockPos piston = ROOT.west();
        BlockPos source = ROOT;
        helper.setBlock(
                piston,
                Blocks.PISTON.defaultBlockState()
                        .setValue(
                                PistonBaseBlock.FACING,
                                helper.getAbsoluteDirection(Direction.EAST)
                        )
        );
        helper.setBlock(source, Blocks.GRANITE);
        helper.setBlock(source.above(), Blocks.DIRT);
        helper.setBlock(
                source.above(2),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );

        helper.runAfterDelay(
                1,
                () -> {
                    helper.setBlock(piston.above(), Blocks.REDSTONE_BLOCK);
                    helper.getLevel().neighborChanged(
                            helper.absolutePos(piston),
                            Blocks.REDSTONE_BLOCK,
                            null
                    );
                }
        );
        helper.succeedWhen(() -> {
            helper.assertFalse(
                    helper.getBlockState(source).is(Blocks.GRANITE),
                    "piston did not move the source"
            );
            helper.assertBlockPresent(Blocks.AIR, source.above(2));
        });
    }

    @GameTest(timeoutTicks = 60)
    @EmptyTemplate
    @TestHolder(description = "Fluid-created source changes invalidate covered loose rocks")
    static void fluidPlacementInvalidatesCoveredLooseRocks(
            ExtendedGameTestHelper helper
    ) {
        BlockPos fluidTarget = ROOT.above();
        BlockPos support = ROOT.above(2);
        BlockPos looseRocks = ROOT.above(3);
        helper.setBlock(ROOT.below(), Blocks.DIAMOND_BLOCK);
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(fluidTarget, Blocks.LAVA);
        helper.setBlock(support, Blocks.DIRT);
        helper.setBlock(
                looseRocks,
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );
        helper.assertTrue(
                helper.getBlockState(looseRocks).canSurvive(
                        helper.getLevel(),
                        helper.absolutePos(looseRocks)
                ),
                "fluid fixture loose rocks were stale before mutation"
        );

        helper.runAfterDelay(
                1,
                () -> {
                    helper.setBlock(fluidTarget.north(), Blocks.WATER);
                    helper.getLevel().neighborChanged(
                            helper.absolutePos(fluidTarget),
                            Blocks.WATER,
                            null
                    );
                }
        );
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.GRANITE, ROOT);
            helper.assertBlockPresent(Blocks.STONE, fluidTarget);
            helper.assertBlockPresent(Blocks.DIRT, support);
            helper.assertBlockPresent(Blocks.AIR, looseRocks);
        });
    }

    @GameTest(timeoutTicks = 60)
    @EmptyTemplate
    @TestHolder(description = "Explosion source destruction invalidates protected covered loose rocks")
    static void explosionInvalidatesProtectedCoveredLooseRocks(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT.below(), Blocks.DIAMOND_BLOCK);
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(ROOT.above(), Blocks.DIRT);
        helper.setBlock(
                ROOT.above(2),
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(LooseRocksBlock.FAMILY, StoneFamily.GRANITE)
        );

        helper.runAfterDelay(1, () -> {
            BlockPos source = helper.absolutePos(ROOT);
            helper.getLevel().explode(
                    null,
                    source.getX() + 0.5,
                    source.getY() + 0.5,
                    source.getZ() + 0.5,
                    2.0F,
                    Level.ExplosionInteraction.BLOCK
            );
        });
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, ROOT);
            helper.assertBlockPresent(Blocks.DIRT, ROOT.above());
            helper.assertBlockPresent(Blocks.AIR, ROOT.above(2));
        });
    }

    @GameTest(timeoutTicks = 100)
    @EmptyTemplate
    @TestHolder(description = "Sticky-piston retraction invalidates source and destination columns")
    static void stickyPistonRetractionInvalidatesBothColumns(
            ExtendedGameTestHelper helper
    ) {
        BlockPos piston = ROOT.west();
        BlockPos destination = ROOT;
        BlockPos source = ROOT.east();
        helper.setBlock(
                piston,
                Blocks.STICKY_PISTON.defaultBlockState()
                        .setValue(
                                PistonBaseBlock.FACING,
                                helper.getAbsoluteDirection(Direction.EAST)
                        )
        );
        helper.setBlock(piston.above(), Blocks.REDSTONE_BLOCK);
        helper.getLevel().neighborChanged(
                helper.absolutePos(piston),
                Blocks.REDSTONE_BLOCK,
                null
        );

        helper.runAfterDelay(4, () -> {
            helper.assertBlockProperty(
                    piston,
                    PistonBaseBlock.EXTENDED,
                    true
            );
            helper.setBlock(source, Blocks.GRANITE);
            helper.setBlock(source.above(), Blocks.DIRT);
            helper.setBlock(
                    source.above(2),
                    ModBlocks.LOOSE_ROCKS.get()
                            .defaultBlockState()
                            .setValue(
                                    LooseRocksBlock.FAMILY,
                                    StoneFamily.GRANITE
                            )
            );
            helper.setBlock(destination.above(), Blocks.DIRT);
            helper.setBlock(
                    destination.above(2),
                    ModBlocks.LOOSE_ROCKS.get()
                            .defaultBlockState()
                            .setValue(
                                    LooseRocksBlock.FAMILY,
                                    StoneFamily.STONE
                            )
            );

            helper.setBlock(piston.above(), Blocks.AIR);
            helper.getLevel().neighborChanged(
                    helper.absolutePos(piston),
                    Blocks.AIR,
                    null
            );
        });
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, source);
            helper.assertBlockPresent(Blocks.GRANITE, destination);
            helper.assertBlockPresent(Blocks.AIR, source.above(2));
            helper.assertBlockPresent(Blocks.AIR, destination.above(2));
        });
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose-rock feature places the resolved family state")
    static void featurePlacesResolvedFamily(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);

        helper.assertTrue(
                placeFeature(helper, ROOT.above()),
                "feature rejected valid Granite support"
        );
        helper.assertBlockPresent(ModBlocks.LOOSE_ROCKS.get(), ROOT.above());
        helper.assertBlockProperty(
                ROOT.above(),
                LooseRocksBlock.FAMILY,
                StoneFamily.GRANITE
        );
        helper.assertFalse(
                helper.getLevel().getBlockTicks().hasScheduledTick(
                        helper.absolutePos(ROOT.above()),
                        ModBlocks.LOOSE_ROCKS.get()
                ),
                "world generation seeded a loose-rock validation tick"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose-rock feature rejects occupied targets")
    static void featureRejectsOccupiedTarget(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(ROOT.above(), Blocks.OBSIDIAN);

        helper.assertFalse(
                placeFeature(helper, ROOT.above()),
                "feature replaced a non-replaceable block"
        );
        helper.assertBlockPresent(Blocks.OBSIDIAN, ROOT.above());
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose-rock feature rejects fluid targets")
    static void featureRejectsFluidTarget(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        helper.setBlock(ROOT.above(), Blocks.WATER);

        helper.assertFalse(
                placeFeature(helper, ROOT.above()),
                "feature placed loose rocks into fluid"
        );
        helper.assertBlockPresent(Blocks.WATER, ROOT.above());
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Loose-rock feature rejects unresolved cover")
    static void featureRejectsUnresolvedCover(ExtendedGameTestHelper helper) {
        BlockPos support = ROOT.above(2);
        helper.setBlock(support, Blocks.DIRT);

        helper.assertFalse(
                placeFeature(helper, support.above()),
                "feature invented a family for unresolved cover"
        );
        helper.assertBlockPresent(Blocks.AIR, support.above());
        helper.succeed();
    }

    private static void assertResolvedSupport(
            ExtendedGameTestHelper helper,
            BlockPos support,
            StoneFamily expected
    ) {
        var resolved = StoneFamilyResolver.resolveSupport(
                helper.getLevel(),
                helper.absolutePos(support)
        );
        helper.assertTrue(resolved.isPresent(), "support did not resolve a family");
        helper.assertValueEqual(
                expected,
                resolved.orElseThrow().family(),
                "resolved support family"
        );
        helper.assertTrue(
                StoneFamilyCatalog.get().byIndex(expected.ordinal()).isPresent(),
                "family was not exposed by catalog index"
        );
    }

    private static void assertExactDrop(
            ExtendedGameTestHelper helper,
            BlockPos position,
            Item expected,
            int count
    ) {
        AABB bounds = new AABB(helper.absolutePos(position)).inflate(2.0);
        int actual = helper.getLevel()
                .getEntitiesOfClass(ItemEntity.class, bounds)
                .stream()
                .filter(entity -> entity.getItem().is(expected))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
        helper.assertValueEqual(count, actual, "family Rock drop count");
    }

    private static boolean placeFeature(
            ExtendedGameTestHelper helper,
            BlockPos position
    ) {
        var level = helper.getLevel();
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                Identifier.fromNamespaceAndPath(
                        MaterialProgression.MOD_ID,
                        "loose_rocks"
                )
        );
        ConfiguredFeature<?, ?> configuredFeature = level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(key)
                .orElseThrow()
                .value();
        return configuredFeature.place(
                level,
                level.getChunkSource().getGenerator(),
                RandomSource.create(42L),
                helper.absolutePos(position)
        );
    }

    private static void assertConfiguredPlacement(
            ExtendedGameTestHelper helper,
            BlockPos position,
            StoneFamily expected,
            String label
    ) {
        helper.assertTrue(
                placeFeature(helper, position),
                label + " configured feature rejected placement"
        );
        helper.assertBlockPresent(ModBlocks.LOOSE_ROCKS.get(), position);
        helper.assertBlockProperty(
                position,
                LooseRocksBlock.FAMILY,
                expected
        );
    }

    private record PlacementCase(
            String label,
            Block support,
            StoneFamily expected
    ) {
    }

    private record CoverPlacementCase(
            String label,
            Block cover,
            Block nearestSource,
            StoneFamily expected
    ) {
    }
}
