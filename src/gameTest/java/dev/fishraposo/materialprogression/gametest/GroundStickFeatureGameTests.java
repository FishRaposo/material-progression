package dev.fishraposo.materialprogression.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.world.level.levelgen.feature.configurations.GroundStickConfiguration;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.AABB;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GroundStickFeatureGameTests {
    private static final Identifier DENSITY_FEATURE =
            Identifier.fromNamespaceAndPath(
                    MaterialProgressionGameTestMod.MOD_ID,
                    "ground_stick_density"
            );
    private static final Identifier NEAR_FEATURE = Identifier.fromNamespaceAndPath(
            MaterialProgressionGameTestMod.MOD_ID,
            "ground_stick_near"
    );
    private static final Identifier BOUNDED_FEATURE =
            Identifier.fromNamespaceAndPath(
                    MaterialProgressionGameTestMod.MOD_ID,
                    "ground_stick_bounded"
            );
    private static final TagKey<Block> GROUND_STICK_ANCHORS = TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "ground_stick_anchors"
            )
    );
    private static final BlockPos CENTER = new BlockPos(4, 2, 4);

    private GroundStickFeatureGameTests() {
    }

    @GameTest
    @EmptyTemplate(value = "9x9x8")
    @TestHolder(description = "Ground Sticks are materially denser near trees")
    static void registeredFeatureMateriallyPrefersTrees(
            ExtendedGameTestHelper helper
    ) {
        prepareFlatGround(helper, 1, 7);
        setDensityAnchors(helper, Blocks.OAK_LOG);
        assertAnchorTagged(
                helper,
                new BlockPos(2, 2, 2),
                "density tree"
        );
        int nearSuccesses = trialSuccesses(
                helper,
                DENSITY_FEATURE,
                80,
                1,
                7
        );

        setDensityAnchors(helper, Blocks.AIR);
        clearGroundSticks(helper, 1, 7);
        int farSuccesses = trialSuccesses(
                helper,
                DENSITY_FEATURE,
                80,
                1,
                7
        );

        helper.assertTrue(
                nearSuccesses >= 60,
                "tree fixture produced only " + nearSuccesses
                        + " successful clusters out of 80"
        );
        helper.assertTrue(
                farSuccesses <= 35,
                "bare ground produced " + farSuccesses
                        + " successful clusters out of 80"
        );
        helper.assertTrue(
                nearSuccesses >= farSuccesses + 30,
                "near/far density separation was too small: "
                        + nearSuccesses + " versus " + farSuccesses
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "9x9x8")
    @TestHolder(description = "Shrubs and datapack tag extensions are anchors")
    static void shrubsAndTagExtensionsAnchorGroundSticks(
            ExtendedGameTestHelper helper
    ) {
        prepareFlatGround(helper, 1, 7);

        helper.setBlock(CENTER.east(), Blocks.SWEET_BERRY_BUSH);
        assertAnchorTagged(
                helper,
                CENTER.east(),
                "sweet berry"
        );
        helper.assertTrue(
                placeFeature(helper, NEAR_FEATURE, CENTER, 41L),
                "sweet berry bush was not treated as an anchor"
        );
        helper.assertBlockPresent(ModBlocks.GROUND_STICK.get(), CENTER);

        helper.setBlock(CENTER, Blocks.AIR);
        helper.setBlock(CENTER.east(), Blocks.AIR);
        helper.setBlock(
                CENTER.west(),
                MaterialProgressionGameTestMod.EXTERNAL_DIRECT_SURFACE.get()
        );
        helper.assertTrue(
                placeFeature(helper, NEAR_FEATURE, CENTER, 42L),
                "test datapack anchor extension was not honored"
        );
        helper.assertBlockPresent(ModBlocks.GROUND_STICK.get(), CENTER);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "9x9x8")
    @TestHolder(description = "Bare surfaces and caves have no generic fallback")
    static void deterministicFeatureRejectsBareAndBuriedGround(
            ExtendedGameTestHelper helper
    ) {
        prepareFlatGround(helper, 1, 7);
        helper.assertTrue(
                !placeFeature(helper, NEAR_FEATURE, CENTER, 43L),
                "bare surface received a Ground Stick with zero background chance"
        );

        for (int x = 2; x <= 6; x++) {
            for (int z = 2; z <= 6; z++) {
                helper.setBlock(new BlockPos(x, 5, z), Blocks.STONE);
            }
        }
        helper.assertTrue(
                !placeFeature(helper, NEAR_FEATURE, CENTER, 44L),
                "buried no-anchor fixture received a Ground Stick"
        );
        helper.assertBlockNotPresent(ModBlocks.GROUND_STICK.get(), CENTER);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "9x9x8")
    @TestHolder(description = "Configured attempts stay bounded by count and spread")
    static void placementCountAndRadiusStayBounded(
            ExtendedGameTestHelper helper
    ) {
        prepareFlatGround(helper, 1, 7);
        helper.setBlock(CENTER.east(), Blocks.OAK_LOG);
        assertAnchorTagged(
                helper,
                CENTER.east(),
                "bounded tree"
        );
        helper.assertTrue(
                placeFeature(helper, BOUNDED_FEATURE, CENTER, 45L),
                "bounded deterministic feature placed nothing"
        );

        List<BlockPos> positions = groundStickPositions(helper, 1, 7);
        helper.assertTrue(
                !positions.isEmpty() && positions.size() <= 4,
                "four attempts produced " + positions.size() + " placements"
        );
        for (BlockPos position : positions) {
            helper.assertTrue(
                    Math.abs(position.getX() - CENTER.getX()) <= 2
                            && Math.abs(position.getZ() - CENTER.getZ()) <= 2,
                    "placement escaped configured spread: " + position
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Ground Stick configuration rejects unsafe bounds")
    static void configurationCodecRejectsUnsafeBounds(
            ExtendedGameTestHelper helper
    ) {
        JsonObject valid = encodedProductionShape();
        assertInvalid(helper, valid, "attempts", 0);
        assertInvalid(helper, valid, "attempts", 33);
        assertInvalid(helper, valid, "horizontal_spread", 17);
        assertInvalid(helper, valid, "surface_vertical_range", -1);
        assertInvalid(helper, valid, "surface_vertical_range", 9);
        assertInvalid(helper, valid, "anchor_horizontal_radius", 0);
        assertInvalid(helper, valid, "anchor_horizontal_radius", 9);
        assertInvalid(helper, valid, "anchor_vertical_radius", 5);
        assertInvalid(helper, valid, "near_chance", 1.01F);
        assertInvalid(helper, valid, "background_chance", -0.01F);

        JsonObject inverted = valid.deepCopy();
        inverted.addProperty("near_chance", 0.1F);
        inverted.addProperty("background_chance", 0.5F);
        helper.assertTrue(
                GroundStickConfiguration.CODEC.parse(
                        JsonOps.INSTANCE,
                        inverted
                ).error().isPresent(),
                "near chance below background chance decoded successfully"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Support removal drops exactly one Ground Stick")
    static void supportRemovalDropsExactlyOneStick(
            ExtendedGameTestHelper helper
    ) {
        BlockPos support = new BlockPos(1, 1, 1);
        BlockPos stick = support.above();
        helper.setBlock(support, Blocks.DIRT);
        helper.setBlock(stick, ModBlocks.GROUND_STICK.get());
        helper.setBlock(support, Blocks.AIR);
        helper.assertBlockPresent(Blocks.AIR, stick);

        AABB bounds = new AABB(helper.absolutePos(stick)).inflate(2.0);
        int dropped = helper.getLevel()
                .getEntitiesOfClass(ItemEntity.class, bounds)
                .stream()
                .filter(entity -> entity.getItem().is(Items.STICK))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
        helper.assertValueEqual(1, dropped, "Ground Stick support-removal drop");
        helper.succeed();
    }

    private static int trialSuccesses(
            ExtendedGameTestHelper helper,
            Identifier feature,
            int trials,
            int min,
            int max
    ) {
        int successes = 0;
        for (int trial = 0; trial < trials; trial++) {
            if (placeFeature(helper, feature, CENTER, 1000L + trial)) {
                successes++;
            }
            clearGroundSticks(helper, min, max);
        }
        return successes;
    }

    private static void setDensityAnchors(
            ExtendedGameTestHelper helper,
            Block block
    ) {
        helper.setBlock(new BlockPos(2, 2, 2), block);
        helper.setBlock(new BlockPos(6, 2, 2), block);
        helper.setBlock(new BlockPos(2, 2, 6), block);
        helper.setBlock(new BlockPos(6, 2, 6), block);
    }

    private static void assertAnchorTagged(
            ExtendedGameTestHelper helper,
            BlockPos anchor,
            String label
    ) {
        BlockPos absoluteAnchor = helper.absolutePos(anchor);
        var level = helper.getLevel();
        helper.assertTrue(
                level.getBlockState(absoluteAnchor).is(GROUND_STICK_ANCHORS),
                label + " anchor " + absoluteAnchor + " state "
                        + level.getBlockState(absoluteAnchor)
                        + " is not in " + GROUND_STICK_ANCHORS
        );
    }

    private static void prepareFlatGround(
            ExtendedGameTestHelper helper,
            int min,
            int max
    ) {
        for (int x = min; x <= max; x++) {
            for (int z = min; z <= max; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.DIRT);
                for (int y = 2; y <= 7; y++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.AIR);
                }
            }
        }
    }

    private static void clearGroundSticks(
            ExtendedGameTestHelper helper,
            int min,
            int max
    ) {
        for (int x = min; x <= max; x++) {
            for (int z = min; z <= max; z++) {
                BlockPos position = new BlockPos(x, 2, z);
                if (helper.getBlockState(position).is(ModBlocks.GROUND_STICK.get())) {
                    helper.setBlock(position, Blocks.AIR);
                }
            }
        }
    }

    private static List<BlockPos> groundStickPositions(
            ExtendedGameTestHelper helper,
            int min,
            int max
    ) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = min; x <= max; x++) {
            for (int z = min; z <= max; z++) {
                BlockPos position = new BlockPos(x, 2, z);
                if (helper.getBlockState(position).is(ModBlocks.GROUND_STICK.get())) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    private static boolean placeFeature(
            ExtendedGameTestHelper helper,
            Identifier id,
            BlockPos position,
            long seed
    ) {
        var level = helper.getLevel();
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                id
        );
        ConfiguredFeature<?, ?> feature = level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(key)
                .orElseThrow()
                .value();
        return feature.place(
                level,
                level.getChunkSource().getGenerator(),
                RandomSource.create(seed),
                helper.absolutePos(position)
        );
    }

    private static JsonObject encodedProductionShape() {
        GroundStickConfiguration configuration =
                new GroundStickConfiguration(
                        BlockStateProvider.simple(
                                ModBlocks.GROUND_STICK.get()
                                        .defaultBlockState()
                        ),
                        TagKey.create(
                                Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(
                                        MaterialProgression.MOD_ID,
                                        "ground_stick_anchors"
                                )
                        ),
                        12,
                        7,
                        4,
                        5,
                        3,
                        0.55F,
                        0.02F
                );
        JsonElement encoded = GroundStickConfiguration.CODEC
                .encodeStart(JsonOps.INSTANCE, configuration)
                .result()
                .orElseThrow();
        return encoded.getAsJsonObject();
    }

    private static void assertInvalid(
            ExtendedGameTestHelper helper,
            JsonObject valid,
            String field,
            Number value
    ) {
        JsonObject invalid = valid.deepCopy();
        invalid.addProperty(field, value);
        helper.assertTrue(
                GroundStickConfiguration.CODEC.parse(
                        JsonOps.INSTANCE,
                        invalid
                ).error().isPresent(),
                field + "=" + value + " decoded successfully"
        );
    }
}
