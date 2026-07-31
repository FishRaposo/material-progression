package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.stone.GeologyDimensionProfileDefinition;
import dev.fishraposo.materialprogression.stone.GeologyDimensionProfileReloadListener;
import dev.fishraposo.materialprogression.stone.GeologyMiningPredictionCache;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotPayload;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotRequest;
import dev.fishraposo.materialprogression.stone.GeologyMiningTarget;
import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.GeologyTierResolver;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GeologyDimensionProfileGameTests {
    private static final Identifier RESOURCE_ID =
            Identifier.fromNamespaceAndPath(
                    MaterialProgressionGameTestMod.MOD_ID,
                    "custom_depths"
            );
    private static final Identifier DUPLICATE_RESOURCE_ID =
            Identifier.fromNamespaceAndPath(
                    MaterialProgressionGameTestMod.MOD_ID,
                    "duplicate_custom_depths"
            );
    private static final ResourceKey<Level> CUSTOM_DIMENSION =
            ResourceKey.create(
                    Registries.DIMENSION,
                    Identifier.fromNamespaceAndPath(
                            MaterialProgressionGameTestMod.MOD_ID,
                            "layered_world"
                    )
            );
    private static final ResourceKey<Level> UNCONFIGURED_DIMENSION =
            ResourceKey.create(
                    Registries.DIMENSION,
                    Identifier.fromNamespaceAndPath(
                            MaterialProgressionGameTestMod.MOD_ID,
                            "unconfigured_world"
                    )
            );

    private GeologyDimensionProfileGameTests() {
    }

    @GameTest(batch = "geology_dimension_profile_mutation")
    @EmptyTemplate
    @TestHolder(
            description = "Custom geology profiles reload atomically and resolve immediately"
    )
    static void customProfilesReloadTransactionally(
            ExtendedGameTestHelper helper
    ) {
        GeologyDimensionProfileReloadListener listener =
                new GeologyDimensionProfileReloadListener();
        try {
            reloadProfilePack(
                    "custom_depths",
                    """
                            {
                              "dimension": "material_progression_gametests:layered_world",
                              "bands": [
                                {"minimum_y": 64, "level": 0},
                                {"minimum_y": 32, "level": 1},
                                {"minimum_y": 0, "level": 2},
                                {"level": 3}
                              ]
                            }
                            """
            );

            assertTier(helper, CUSTOM_DIMENSION, 64, 0, false, 0);
            assertTier(helper, CUSTOM_DIMENSION, 63, 0, false, 1);
            assertTier(helper, CUSTOM_DIMENSION, 32, 0, false, 1);
            assertTier(helper, CUSTOM_DIMENSION, 31, 0, false, 2);
            assertTier(helper, CUSTOM_DIMENSION, 0, -1, false, 1);
            assertTier(helper, CUSTOM_DIMENSION, -1, 0, false, 3);
            assertTier(helper, CUSTOM_DIMENSION, 32, 1, true, 1);
            assertTier(helper, CUSTOM_DIMENSION, -64, 1, false, 3);
            assertTier(helper, UNCONFIGURED_DIMENSION, -64, 0, false, 0);
            helper.assertValueEqual(
                    3,
                    GeologyTierResolver.naturalTier(
                            CUSTOM_DIMENSION,
                            Identifier.fromNamespaceAndPath(
                                    MaterialProgressionGameTestMod.MOD_ID,
                                    "slate"
                            ),
                            2,
                            32,
                            false
                    ).level(),
                    "external family custom-dimension geology tier"
            );
            GeologyMiningPredictionCache prediction =
                    new GeologyMiningPredictionCache();
            GeologyMiningTarget customTarget = new GeologyMiningTarget(
                    CUSTOM_DIMENSION.identifier(),
                    net.minecraft.core.BlockPos.ZERO,
                    Block.getId(Blocks.STONE.defaultBlockState())
            );
            GeologyMiningSnapshotRequest predictionRequest =
                    prediction.beginTarget(customTarget, true, 10L);
            helper.assertTrue(
                    prediction.accept(
                            GeologyMiningSnapshotPayload.resolved(
                                    predictionRequest,
                                    true,
                                    java.util.Optional.of(
                                            GeologyTierResolver.naturalTier(
                                                    CUSTOM_DIMENSION,
                                                    Identifier
                                                            .fromNamespaceAndPath(
                                                                    MaterialProgressionGameTestMod.MOD_ID,
                                                                    "slate"
                                                            ),
                                                    2,
                                                    32,
                                                    false
                                            )
                                    ),
                                    1L,
                                    1L
                            ),
                            11L
                    ),
                    "custom-dimension snapshot was rejected"
            );
            helper.assertValueEqual(
                    2.0F,
                    prediction.adjustSpeed(
                            12.0F,
                            customTarget,
                            true,
                            11L
                    ),
                    "custom-dimension external-family predicted speed"
            );

            assertTier(helper, Level.OVERWORLD, 49, 0, false, 0);
            assertTier(helper, Level.OVERWORLD, 48, 0, false, 1);
            assertTier(helper, Level.NETHER, 96, 0, false, 0);
            assertTier(helper, Level.NETHER, 95, 0, false, 1);
            assertTier(helper, Level.END, 80, 0, false, 2);
            assertTier(helper, Level.END, 80, 0, true, 1);

            GeologyDimensionProfileDefinition changed = definition(
                    CUSTOM_DIMENSION,
                    List.of(band(80, 3), catchAll(1))
            );
            reloadProfilePack(
                    "custom_depths",
                    """
                            {
                              "dimension": "material_progression_gametests:layered_world",
                              "bands": [
                                {"minimum_y": 80, "level": 3},
                                {"level": 1}
                              ]
                            }
                            """
            );
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 3);
            assertTier(helper, CUSTOM_DIMENSION, 79, 0, false, 1);

            boolean duplicateRejected = false;
            try {
                listener.apply(
                        Map.of(
                                RESOURCE_ID, changed,
                                DUPLICATE_RESOURCE_ID, changed
                        ),
                        null,
                        Profiler.get()
                );
            } catch (IllegalStateException expected) {
                duplicateRejected = true;
            }
            helper.assertTrue(
                    duplicateRejected,
                    "duplicate dimension ownership was accepted"
            );
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 3);

            boolean builtInRejected = false;
            try {
                listener.apply(
                        Map.of(
                                RESOURCE_ID,
                                definition(
                                        Level.OVERWORLD,
                                        List.of(catchAll(3))
                                )
                        ),
                        null,
                        Profiler.get()
                );
            } catch (IllegalStateException expected) {
                builtInRejected = true;
            }
            helper.assertTrue(
                    builtInRejected,
                    "built-in dimension override was accepted"
            );
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 3);

            helper.assertTrue(
                    malformedReloadRejected(
                            "non_descending",
                            """
                                    {
                                      "dimension": "material_progression_gametests:layered_world",
                                      "bands": [
                                        {"minimum_y": 32, "level": 1},
                                        {"minimum_y": 64, "level": 2},
                                        {"level": 3}
                                      ]
                                    }
                                    """
                    ),
                    "non-descending profile thresholds reloaded successfully"
            );
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 3);

            helper.assertTrue(
                    malformedReloadRejected(
                            "duplicate_threshold",
                            """
                                    {
                                      "dimension": "material_progression_gametests:layered_world",
                                      "bands": [
                                        {"minimum_y": 32, "level": 1},
                                        {"minimum_y": 32, "level": 2},
                                        {"level": 3}
                                      ]
                                    }
                                    """
                    ),
                    "duplicate profile threshold reloaded successfully"
            );
            helper.assertTrue(
                    malformedReloadRejected(
                            "empty_bands",
                            """
                                    {
                                      "dimension": "material_progression_gametests:layered_world",
                                      "bands": []
                                    }
                                    """
                    ),
                    "empty profile bands reloaded successfully"
            );
            helper.assertTrue(
                    malformedReloadRejected(
                            "missing_catch_all",
                            """
                                    {
                                      "dimension": "material_progression_gametests:layered_world",
                                      "bands": [
                                        {"minimum_y": 32, "level": 1}
                                      ]
                                    }
                                    """
                    ),
                    "profile without catch-all reloaded successfully"
            );
            helper.assertTrue(
                    malformedReloadRejected(
                            "invalid_tier",
                            """
                                    {
                                      "dimension": "material_progression_gametests:layered_world",
                                      "bands": [{"level": 4}]
                                    }
                                    """
                    ),
                    "out-of-range profile tier reloaded successfully"
            );
            helper.assertTrue(
                    malformedReloadRejected(
                            "invalid_dimension",
                            """
                                    {
                                      "dimension": "not a valid id",
                                      "bands": [{"level": 0}]
                                    }
                                    """
                    ),
                    "invalid profile dimension ID reloaded successfully"
            );
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 3);

            listener.apply(Map.of(), null, Profiler.get());
            assertTier(helper, CUSTOM_DIMENSION, 80, 0, false, 0);
            helper.succeed();
        } finally {
            listener.apply(Map.of(), null, Profiler.get());
        }
    }

    private static GeologyDimensionProfileDefinition definition(
            ResourceKey<Level> dimension,
            List<GeologyDimensionProfileDefinition.Band> bands
    ) {
        return new GeologyDimensionProfileDefinition(
                dimension.identifier(),
                bands
        );
    }

    private static GeologyDimensionProfileDefinition.Band band(
            int minimumY,
            int level
    ) {
        return new GeologyDimensionProfileDefinition.Band(
                Optional.of(minimumY),
                level
        );
    }

    private static GeologyDimensionProfileDefinition.Band catchAll(int level) {
        return new GeologyDimensionProfileDefinition.Band(
                Optional.empty(),
                level
        );
    }

    private static void assertTier(
            ExtendedGameTestHelper helper,
            ResourceKey<Level> dimension,
            int y,
            int familyModifier,
            boolean exposed,
            int expected
    ) {
        GeologyTier tier = GeologyTierResolver.naturalTier(
                dimension,
                StoneFamily.STONE.id(),
                familyModifier,
                y,
                exposed
        );
        helper.assertValueEqual(
                expected,
                tier.level(),
                "geology tier for " + dimension.identifier() + " at y=" + y
        );
    }

    private static boolean malformedReloadRejected(
            String name,
            String json
    ) {
        try {
            reloadProfilePack(name, json);
            return false;
        } catch (RuntimeException expected) {
            return true;
        }
    }

    private static void reloadProfilePack(String name, String json) {
        Path root = null;
        try {
            root = Files.createTempDirectory("geology-profile-reload");
            Path definition = root.resolve(
                    "data/"
                            + MaterialProgressionGameTestMod.MOD_ID
                            + "/geology_dimension_profile/"
                            + name
                            + ".json"
            );
            Files.createDirectories(definition.getParent());
            Files.writeString(definition, json);
            PackLocationInfo location = new PackLocationInfo(
                    "geology-profile-malformed-test",
                    Component.literal("Geology profile malformed test"),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );
            try (var resources = new MultiPackResourceManager(
                    PackType.SERVER_DATA,
                    List.of(new PathPackResources(location, root))
            )) {
                new ReloadProbe().decodeAndApply(resources);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create malformed geology profile pack",
                    exception
            );
        } finally {
            deleteTree(root);
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    throw new IllegalStateException(
                            "Could not remove geology profile test pack",
                            exception
                    );
                }
            });
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not traverse geology profile test pack",
                    exception
            );
        }
    }

    private static final class ReloadProbe
            extends GeologyDimensionProfileReloadListener {
        private void decodeAndApply(ResourceManager resources) {
            apply(
                    prepare(resources, Profiler.get()),
                    resources,
                    Profiler.get()
            );
        }
    }
}
