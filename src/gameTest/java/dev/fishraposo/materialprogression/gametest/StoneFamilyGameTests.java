package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModFeatures;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import dev.fishraposo.materialprogression.stone.StoneResistance;
import dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.resources.Identifier;
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
    @TestHolder(description = "Direct stone-family supports resolve without fallback")
    static void directFamilySupport(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.GRANITE);
        assertResolvedSupport(helper, ROOT, StoneFamily.GRANITE);
        helper.setBlock(ROOT, Blocks.SAND);
        assertResolvedSupport(helper, ROOT, StoneFamily.SANDSTONE);
        helper.setBlock(ROOT, Blocks.RED_SAND);
        assertResolvedSupport(helper, ROOT, StoneFamily.RED_SANDSTONE);

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
    @TestHolder(description = "Soul sand resolves netherrack only through the downward scan")
    static void soulSandScansToNetherrack(ExtendedGameTestHelper helper) {
        helper.setBlock(ROOT, Blocks.NETHERRACK);
        helper.setBlock(ROOT.above(), Blocks.SOUL_SAND);
        assertResolvedSupport(helper, ROOT.above(), StoneFamily.NETHERRACK);
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
        return ModFeatures.LOOSE_ROCKS.get().place(
                new FeaturePlaceContext<>(
                        Optional.empty(),
                        level,
                        level.getChunkSource().getGenerator(),
                        RandomSource.create(42L),
                        helper.absolutePos(position),
                        NoneFeatureConfiguration.INSTANCE
                )
        );
    }
}
