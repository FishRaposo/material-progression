package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.progression.FeedbackMessages;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.GeologyTierResolver;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyDefinition;
import dev.fishraposo.materialprogression.stone.StoneResistance;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class ThirdPartyStoneFamilyGameTests {
    private static final Identifier FAMILY_ID = Identifier.fromNamespaceAndPath(
            MaterialProgressionGameTestMod.MOD_ID,
            "slate"
    );
    private static final BlockPos ROOT = new BlockPos(1, 1, 1);

    private ThirdPartyStoneFamilyGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A datapack-defined third-party family loads through the live catalog")
    static void externalFamilyLoads(ExtendedGameTestHelper helper) {
        StoneFamilyCatalog.Entry entry = StoneFamilyCatalog.get()
                .byId(FAMILY_ID)
                .orElseThrow();
        helper.assertValueEqual(FAMILY_ID, entry.id(), "external family ID");
        helper.assertTrue(entry.builtInFamily().isEmpty(), "external family became built-in");
        helper.assertValueEqual(
                MaterialProgressionGameTestMod.EXTERNAL_ROCK.get(),
                entry.rockItem(),
                "external Rock output"
        );
        helper.assertValueEqual(
                MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get(),
                entry.rawBlock(),
                "external raw block"
        );
        helper.assertValueEqual(
                MaterialProgressionGameTestMod.EXTERNAL_COBBLE.get(),
                entry.cobbledBlock(),
                "external cobble"
        );
        helper.assertValueEqual(
                StoneResistance.HARD,
                entry.resistance().tier(),
                "external resistance"
        );
        helper.assertValueEqual(
                1,
                entry.resistance().modifier(),
                "external additive resistance modifier"
        );
        helper.assertTrue(
                entry.displayName().getContents()
                        instanceof TranslatableContents,
                "external family name is not translatable"
        );
        TranslatableContents displayName =
                (TranslatableContents) entry.displayName().getContents();
        helper.assertValueEqual(
                "stone_family.material_progression_gametests.slate",
                displayName.getKey(),
                "external family translation key"
        );
        helper.assertValueEqual(
                "Slate",
                displayName.getFallback(),
                "external family fallback name"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The external Loose Rocks runtime block creates its persistent entity")
    static void externalBlockCreatesBlockEntity(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.setBlock(ROOT.above(), ModBlocks.EXTERNAL_LOOSE_ROCKS.get());
        helper.assertBlockPresent(ModBlocks.EXTERNAL_LOOSE_ROCKS.get(), ROOT.above());
        helper.getBlockEntity(
                ROOT.above(),
                ExternalLooseRockBlockEntity.class
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Four external Rocks specialize while mixed Rocks use Cobblestone")
    static void externalCobblingUsesFamilyId(ExtendedGameTestHelper helper) {
        assertCrafts(
                helper,
                List.of(
                        externalRock(),
                        externalRock(),
                        externalRock(),
                        externalRock()
                ),
                MaterialProgressionGameTestMod.EXTERNAL_COBBLE.get().asItem()
        );
        assertCrafts(
                helper,
                List.of(
                        externalRock(),
                        externalRock(),
                        new ItemStack(
                                dev.fishraposo.materialprogression.registry.ModItems.ROCK.get()
                        ),
                        externalRock()
                ),
                Items.COBBLESTONE
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "5x5x5")
    @TestHolder(description = "External direct and covered supports persist their family identity")
    static void externalFeaturePlacementStoresIdentity(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_DIRECT_SURFACE.get());
        helper.assertTrue(placeFeature(helper, ROOT.above()), "direct external placement failed");
        assertExternalIdentity(helper, ROOT.above());

        BlockPos covered = ROOT.offset(2, 0, 0);
        helper.setBlock(covered, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.setBlock(covered.above(), Blocks.DIRT);
        helper.assertTrue(
                placeFeature(helper, covered.above(2)),
                "covered external placement failed"
        );
        ExternalLooseRockBlockEntity rocks = assertExternalIdentity(
                helper,
                covered.above(2)
        );

        CompoundTag saved = rocks.saveWithFullMetadata(
                helper.getLevel().registryAccess()
        );
        BlockEntity loaded = BlockEntity.loadStatic(
                helper.absolutePos(covered.above(2)),
                helper.getBlockState(covered.above(2)),
                saved,
                helper.getLevel().registryAccess()
        );
        helper.assertTrue(
                loaded instanceof ExternalLooseRockBlockEntity,
                "external loose-rock block entity did not deserialize"
        );
        helper.assertValueEqual(
                FAMILY_ID,
                ((ExternalLooseRockBlockEntity) loaded).familyId().orElseThrow(),
                "saved external family ID"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 60)
    @EmptyTemplate
    @TestHolder(description = "External Loose Rocks invalidate and drop one configured Rock")
    static void externalSupportInvalidationDropsOnce(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertTrue(placeFeature(helper, ROOT.above()), "external placement failed");
        helper.setBlock(ROOT, Blocks.STONE);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, ROOT.above());
            helper.assertValueEqual(
                    1,
                    itemCount(helper, ROOT.above(), MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()),
                    "support-invalidated external Rock count"
            );
        });
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "External raw sources use resistance and exact Rock fragment output")
    static void externalSourceUsesGeologyAndRockDrops(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        StoneFamilyCatalog.Entry entry = StoneFamilyCatalog.get()
                .bySource(helper.getBlockState(ROOT))
                .orElseThrow();
        helper.assertValueEqual(FAMILY_ID, entry.id(), "source family");
        helper.assertValueEqual(
                2,
                GeologyTierResolver.naturalTier(
                        net.minecraft.world.level.Level.OVERWORLD,
                        entry.id(),
                        entry.resistance().modifier(),
                        48,
                        false
                ).level(),
                "external hard-family tier"
        );

        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(Items.DIAMOND_PICKAXE)
        );
        helper.assertTrue(
                player.gameMode.destroyBlock(helper.absolutePos(ROOT)),
                "external source could not be mined"
        );
        int count = itemCount(
                helper,
                ROOT,
                MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()
        );
        helper.assertTrue(
                count == 2 || count == 3,
                "external Rock fragment count was " + count
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "External sources preserve geology drops and localized family feedback")
    static void externalSourceUsesGenericGeologyPaths(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        StoneFamilyCatalog.Entry entry = StoneFamilyCatalog.get()
                .byId(FAMILY_ID)
                .orElseThrow();
        GeologyTier tier = GeologyTierResolver.naturalTier(
                net.minecraft.world.level.Level.OVERWORLD,
                entry.id(),
                entry.resistance().modifier(),
                48,
                false
        );
        helper.assertValueEqual(2, tier.level(), "external family resistance tier");

        TranslatableContents feedback = (TranslatableContents)
                FeedbackMessages.insufficientGeology(entry, tier).getContents();
        helper.assertValueEqual(
                "message.material_progression.geology.insufficient",
                feedback.getKey(),
                "external geology feedback key"
        );
        helper.assertTrue(
                feedback.getArgs()[1] instanceof Component,
                "external geology family argument was not a component"
        );
        TranslatableContents familyName = (TranslatableContents)
                ((Component) feedback.getArgs()[1]).getContents();
        helper.assertValueEqual(
                "stone_family.material_progression_gametests.slate",
                familyName.getKey(),
                "external geology family translation key"
        );
        helper.assertValueEqual(
                "Slate",
                familyName.getFallback(),
                "external geology family fallback"
        );

        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);

        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        ItemStack fortune = new ItemStack(Items.DIAMOND_PICKAXE);
        enchant(helper, fortune, Enchantments.FORTUNE);
        player.setItemInHand(InteractionHand.MAIN_HAND, fortune);
        helper.assertTrue(
                player.gameMode.destroyBlock(helper.absolutePos(ROOT)),
                "Fortune external source could not be mined"
        );
        helper.assertValueEqual(
                4,
                itemCount(helper, ROOT, MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()),
                "Fortune external Rock count"
        );
        helper.assertValueEqual(
                0,
                itemCount(
                        helper,
                        ROOT,
                        MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get().asItem()
                ),
                "Fortune external raw-block count"
        );
        clearNearbyItems(helper, ROOT);

        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        ItemStack silkTouch = new ItemStack(Items.DIAMOND_PICKAXE);
        enchant(helper, silkTouch, Enchantments.SILK_TOUCH);
        player.setItemInHand(InteractionHand.MAIN_HAND, silkTouch);
        helper.assertTrue(
                player.gameMode.destroyBlock(helper.absolutePos(ROOT)),
                "Silk Touch external source could not be mined"
        );
        helper.assertValueEqual(
                1,
                itemCount(
                        helper,
                        ROOT,
                        MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get().asItem()
                ),
                "Silk Touch external raw-block count"
        );
        helper.assertValueEqual(
                0,
                itemCount(helper, ROOT, MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()),
                "Silk Touch external Rock count"
        );
        clearNearbyItems(helper, ROOT);

        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(Items.IRON_SWORD)
        );
        player.gameMode.destroyBlock(helper.absolutePos(ROOT));
        helper.assertValueEqual(
                0,
                allItemCount(helper, ROOT),
                "wrong-tool external drop count"
        );
        clearNearbyItems(helper, ROOT);

        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        for (net.minecraft.core.Direction direction
                : net.minecraft.core.Direction.values()) {
            helper.setBlock(ROOT.relative(direction), Blocks.STONE);
        }
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertValueEqual(
                3,
                GeologyTierResolver.resolve(
                        helper.getLevel(),
                        helper.absolutePos(ROOT),
                        helper.getBlockState(ROOT)
                ).orElseThrow().level(),
                "enclosed external geology tier"
        );
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(Items.STONE_PICKAXE)
        );
        player.gameMode.destroyBlock(helper.absolutePos(ROOT));
        helper.assertValueEqual(
                0,
                allItemCount(helper, ROOT),
                "insufficient-tool external drop count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "5x5x5")
    @TestHolder(description = "External Rock drops do not duplicate for player, creative, or explosion breaks")
    static void externalLooseRockDropPathsAreExact(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertTrue(placeFeature(helper, ROOT.above()), "survival fixture placement");
        helper.breakBlock(
                ROOT.above(),
                ItemStack.EMPTY,
                helper.makeMockPlayer()
        );
        helper.assertValueEqual(
                1,
                itemCount(helper, ROOT.above(), MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()),
                "survival external Rock count"
        );
        clearNearbyItems(helper, ROOT);

        BlockPos creativePos = ROOT;
        helper.setBlock(creativePos, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertTrue(placeFeature(helper, creativePos.above()), "creative fixture placement");
        var creative = helper.makeTickingMockServerPlayerInLevel(GameType.CREATIVE);
        creative.gameMode.destroyBlock(helper.absolutePos(creativePos.above()));
        helper.assertValueEqual(
                0,
                itemCount(helper, creativePos.above(), MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()),
                "creative external Rock count"
        );

        BlockPos explosionPos = ROOT;
        helper.setBlock(explosionPos, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertTrue(placeFeature(helper, explosionPos.above()), "explosion fixture placement");
        helper.getLevel().explode(
                null,
                helper.absolutePos(explosionPos.above()).getX() + 0.5,
                helper.absolutePos(explosionPos.above()).getY() + 0.5,
                helper.absolutePos(explosionPos.above()).getZ() + 0.5,
                2.0F,
                net.minecraft.world.level.Level.ExplosionInteraction.BLOCK
        );
        helper.assertValueEqual(
                1,
                itemCount(
                        helper,
                        explosionPos.above(),
                        MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()
                ),
                "explosion external Rock count"
        );
        helper.succeed();
    }

    @GameTest(
            timeoutTicks = 60,
            batch = "stone_family_catalog_mutation"
    )
    @EmptyTemplate
    @TestHolder(description = "Changing or removing an external family reconciles placed rocks")
    static void familyReloadReconcilesPlacedExternalRock(
            ExtendedGameTestHelper helper
    ) {
        Map<Identifier, StoneFamilyDefinition> original =
                StoneFamilyCatalogFixture.definitionsFrom(StoneFamilyCatalog.get());
        StoneFamilyDefinition external = original.get(FAMILY_ID);
        helper.setBlock(ROOT, MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get());
        helper.assertTrue(placeFeature(helper, ROOT.above()), "external placement failed");

        Map<Identifier, StoneFamilyDefinition> changed = replacing(
                original,
                externalWith(
                        external,
                        external.sourceBlockTag(),
                        TagKey.create(
                                Registries.ITEM,
                                Identifier.parse("c:rocks/slate_alternate")
                        ),
                        external.cobbledBlock(),
                        external.rawBlock(),
                        external.looseRockSurfaceBlockTag(),
                        external.resistance()
                )
        );
        StoneFamilyCatalogFixture.publish(helper, changed);
        helper.runAfterDelay(2, () -> {
            try {
                ExternalLooseRockBlockEntity rocks = assertExternalIdentity(
                        helper,
                        ROOT.above(),
                        MaterialProgressionGameTestMod
                                .EXTERNAL_ROCK_ALTERNATE.get()
                );
                GameTestSupport.assertStack(
                        helper,
                        rocks.rock(),
                        MaterialProgressionGameTestMod
                                .EXTERNAL_ROCK_ALTERNATE.get(),
                        1,
                        "reconciled replacement Rock"
                );
            } catch (RuntimeException | Error failure) {
                StoneFamilyCatalogFixture.publish(helper, original);
                throw failure;
            }

            Map<Identifier, StoneFamilyDefinition> removed =
                    new HashMap<>(changed);
            removed.remove(FAMILY_ID);
            StoneFamilyCatalogFixture.publish(helper, removed);
            helper.runAfterDelay(2, () -> {
                try {
                helper.assertBlockPresent(Blocks.AIR, ROOT.above());
                helper.assertValueEqual(
                        1,
                        itemCount(
                                helper,
                                ROOT.above(),
                                MaterialProgressionGameTestMod
                                        .EXTERNAL_ROCK_ALTERNATE.get()
                        ),
                        "reconciled external Rock count"
                );
                } finally {
                    StoneFamilyCatalogFixture.publish(helper, original);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(
            timeoutTicks = 60,
            batch = "stone_family_incompatible_reload"
    )
    @EmptyTemplate
    @TestHolder(description = "An incompatible family reload drops the previously stored Rock")
    static void incompatibleReloadDropsPreviouslyStoredRock(
            ExtendedGameTestHelper helper
    ) {
        Map<Identifier, StoneFamilyDefinition> original =
                StoneFamilyCatalogFixture.definitionsFrom(
                        StoneFamilyCatalog.get()
                );
        StoneFamilyDefinition external = original.get(FAMILY_ID);
        helper.setBlock(
                ROOT,
                MaterialProgressionGameTestMod.EXTERNAL_DIRECT_SURFACE.get()
        );
        helper.assertTrue(
                placeFeature(helper, ROOT.above()),
                "external direct-surface placement failed"
        );
        assertExternalIdentity(helper, ROOT.above());

        Map<Identifier, StoneFamilyDefinition> changed = replacing(
                original,
                externalWith(
                        external,
                        external.sourceBlockTag(),
                        TagKey.create(
                                Registries.ITEM,
                                Identifier.parse("c:rocks/slate_alternate")
                        ),
                        external.cobbledBlock(),
                        external.rawBlock(),
                        blockTag(
                                "loose_rock_surfaces/slate_raw_only"
                        ),
                        external.resistance()
                )
        );
        StoneFamilyCatalogFixture.publish(helper, changed);
        helper.runAfterDelay(2, () -> {
            try {
                helper.assertBlockPresent(Blocks.AIR, ROOT.above());
                helper.assertValueEqual(
                        1,
                        itemCount(
                                helper,
                                ROOT.above(),
                                MaterialProgressionGameTestMod
                                        .EXTERNAL_ROCK.get()
                        ),
                        "previously stored external Rock count"
                );
                helper.assertValueEqual(
                        0,
                        itemCount(
                                helper,
                                ROOT.above(),
                                MaterialProgressionGameTestMod
                                        .EXTERNAL_ROCK_ALTERNATE.get()
                        ),
                        "replacement external Rock count"
                );
            } finally {
                StoneFamilyCatalogFixture.publish(helper, original);
            }
            helper.succeed();
        });
    }

    @GameTest(
            timeoutTicks = 80,
            batch = "stone_family_strict_reload"
    )
    @EmptyTemplate
    @TestHolder(description = "Malformed and invalid family packs retain the exact published runtime state")
    static void failedResourceReloadRetainsCatalogAndPlacedRock(
            ExtendedGameTestHelper helper
    ) {
        Map<Identifier, StoneFamilyDefinition> original =
                StoneFamilyCatalogFixture.definitionsFrom(
                        StoneFamilyCatalog.get()
                );
        StoneFamilyDefinition external = original.get(FAMILY_ID);
        try {
            StoneFamilyReloadPackFixture.reloadAndPublish(
                    helper,
                    StoneFamilyReloadPackFixture.encode(original)
            );
            StoneFamilyCatalog published = StoneFamilyCatalog.get();
            long publishedVersion = StoneFamilyCatalog.version();

            helper.setBlock(
                    ROOT,
                    MaterialProgressionGameTestMod.EXTERNAL_RAW_STONE.get()
            );
            helper.assertTrue(
                    placeFeature(helper, ROOT.above()),
                    "external Rock placement before failed reload"
            );
            ExternalLooseRockBlockEntity placed = assertExternalIdentity(
                    helper,
                    ROOT.above()
            );

            Map<Identifier, StoneFamilyDefinition> alternateDefinitions =
                    replacing(
                            original,
                            externalWith(
                                    external,
                                    external.sourceBlockTag(),
                                    TagKey.create(
                                            Registries.ITEM,
                                            Identifier.parse(
                                                    "c:rocks/slate_alternate"
                                            )
                                    ),
                                    external.cobbledBlock(),
                                    external.rawBlock(),
                                    external.looseRockSurfaceBlockTag(),
                                    external.resistance()
                            )
                    );

            Map<Identifier, String> malformedSyntax =
                    new HashMap<>(
                            StoneFamilyReloadPackFixture.encode(
                                    alternateDefinitions
                            )
                    );
            malformedSyntax.put(
                    Identifier.fromNamespaceAndPath(
                            MaterialProgressionGameTestMod.MOD_ID,
                            "malformed_syntax"
                    ),
                    "{"
            );
            assertFailedResourceReload(
                    helper,
                    malformedSyntax,
                    "malformed_syntax",
                    published,
                    publishedVersion,
                    placed
            );

            Map<Identifier, String> malformedShape =
                    new HashMap<>(
                            StoneFamilyReloadPackFixture.encode(
                                    alternateDefinitions
                            )
                    );
            malformedShape.put(
                    Identifier.fromNamespaceAndPath(
                            MaterialProgressionGameTestMod.MOD_ID,
                            "malformed_shape"
                    ),
                    "{}"
            );
            assertFailedResourceReload(
                    helper,
                    malformedShape,
                    "malformed_shape",
                    published,
                    publishedVersion,
                    placed
            );

            Map<Identifier, String> invalidModifier =
                    new HashMap<>(
                            StoneFamilyReloadPackFixture.encode(original)
                    );
            invalidModifier.put(
                    FAMILY_ID,
                    StoneFamilyReloadPackFixture.withResistanceModifier(
                            external,
                            4
                    )
            );
            assertFailedResourceReload(
                    helper,
                    invalidModifier,
                    "-3 through 3",
                    published,
                    publishedVersion,
                    placed
            );

            Map<Identifier, StoneFamilyDefinition> duplicateSource =
                    replacing(
                            original,
                            externalWith(
                                    external,
                                    blockTag("duplicate_source"),
                                    external.rockItemTag(),
                                    external.cobbledBlock(),
                                    external.rawBlock(),
                                    external.looseRockSurfaceBlockTag(),
                                    external.resistance()
                            )
                    );
            assertFailedResourceReload(
                    helper,
                    StoneFamilyReloadPackFixture.encode(duplicateSource),
                    "source block is already owned by",
                    published,
                    publishedVersion,
                    placed
            );

            Map<Identifier, StoneFamilyDefinition> duplicateSurface =
                    replacing(
                            original,
                            externalWith(
                                    external,
                                    external.sourceBlockTag(),
                                    external.rockItemTag(),
                                    external.cobbledBlock(),
                                    external.rawBlock(),
                                    blockTag("duplicate_surface"),
                                    external.resistance()
                            )
                    );
            assertFailedResourceReload(
                    helper,
                    StoneFamilyReloadPackFixture.encode(duplicateSurface),
                    "direct surface is already owned by",
                    published,
                    publishedVersion,
                    placed
            );

            helper.runAfterDelay(2, () -> {
                try {
                    assertPublishedStateUnchanged(
                            helper,
                            published,
                            publishedVersion,
                            placed
                    );
                } finally {
                    StoneFamilyCatalogFixture.publish(helper, original);
                }
                helper.succeed();
            });
        } catch (RuntimeException | Error failure) {
            StoneFamilyCatalogFixture.publish(helper, original);
            throw failure;
        }
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Invalid external definitions are rejected without replacing the catalog")
    static void externalValidationIsTransactional(
            ExtendedGameTestHelper helper
    ) {
        Map<Identifier, StoneFamilyDefinition> original =
                StoneFamilyCatalogFixture.definitionsFrom(StoneFamilyCatalog.get());
        StoneFamilyCatalog published = StoneFamilyCatalog.get();
        StoneFamilyDefinition external = original.get(FAMILY_ID);

        Map<String, Map<Identifier, StoneFamilyDefinition>> invalid = new HashMap<>();
        invalid.put(
                "source block is already owned by",
                replacing(
                        original,
                        externalWith(
                                external,
                                blockTag("duplicate_source"),
                                external.rockItemTag(),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "direct surface is already owned by",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                external.rockItemTag(),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                blockTag("duplicate_surface"),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "resolved to 0",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                TagKey.create(
                                        Registries.ITEM,
                                        Identifier.parse("c:rocks/empty")
                                ),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "c:rocks/missing] does not exist",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                TagKey.create(
                                        Registries.ITEM,
                                        Identifier.parse("c:rocks/missing")
                                ),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "resolved to ",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                TagKey.create(Registries.ITEM, Identifier.parse("c:rocks")),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "must also belong to #c:rocks",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                TagKey.create(
                                        Registries.ITEM,
                                        Identifier.parse(
                                                "c:rocks/unparented"
                                        )
                                ),
                                external.cobbledBlock(),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "cobbled block material_progression_gametests:missing_cobble is not registered",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                external.rockItemTag(),
                                Identifier.parse("material_progression_gametests:missing_cobble"),
                                external.rawBlock(),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );
        invalid.put(
                "raw block material_progression_gametests:missing_raw is not registered",
                replacing(
                        original,
                        externalWith(
                                external,
                                external.sourceBlockTag(),
                                external.rockItemTag(),
                                external.cobbledBlock(),
                                Identifier.parse(
                                        "material_progression_gametests:missing_raw"
                                ),
                                external.looseRockSurfaceBlockTag(),
                                external.resistance()
                        )
                )
        );

        for (var candidate : invalid.entrySet()) {
            boolean rejected = false;
            try {
                StoneFamilyCatalogFixture.publish(helper, candidate.getValue());
            } catch (IllegalStateException expected) {
                rejected = expected.getMessage().contains(candidate.getKey());
            }
            helper.assertTrue(rejected, "missing validation error: " + candidate.getKey());
            helper.assertTrue(
                    StoneFamilyCatalog.get() == published,
                    "failed external staging replaced the published catalog"
            );
        }
        for (int modifier : new int[] {-4, 4}) {
            boolean rejected = false;
            try {
                new StoneFamilyDefinition.Resistance(modifier);
            } catch (IllegalArgumentException expected) {
                rejected = expected.getMessage().contains("-3 through 3");
            }
            helper.assertTrue(
                    rejected,
                    "invalid resistance modifier was accepted: " + modifier
            );
        }
        helper.succeed();
    }

    private static Map<Identifier, StoneFamilyDefinition> replacing(
            Map<Identifier, StoneFamilyDefinition> original,
            StoneFamilyDefinition external
    ) {
        Map<Identifier, StoneFamilyDefinition> changed = new HashMap<>(original);
        changed.put(FAMILY_ID, external);
        return changed;
    }

    private static StoneFamilyDefinition externalWith(
            StoneFamilyDefinition original,
            TagKey<Block> source,
            TagKey<Item> rock,
            Identifier cobble,
            Identifier raw,
            TagKey<Block> surface,
            StoneFamilyDefinition.Resistance resistance
    ) {
        return new StoneFamilyDefinition(
                source,
                rock,
                cobble,
                raw,
                surface,
                resistance
        );
    }

    private static void assertFailedResourceReload(
            ExtendedGameTestHelper helper,
            Map<Identifier, String> resources,
            String expectedError,
            StoneFamilyCatalog published,
            long publishedVersion,
            ExternalLooseRockBlockEntity placed
    ) {
        boolean rejected = false;
        try {
            StoneFamilyReloadPackFixture.reloadAndPublish(helper, resources);
        } catch (IllegalStateException expected) {
            rejected = expected.getMessage().contains(expectedError);
        }
        helper.assertTrue(
                rejected,
                "resource reload did not reject with " + expectedError
        );
        assertPublishedStateUnchanged(
                helper,
                published,
                publishedVersion,
                placed
        );
    }

    private static void assertPublishedStateUnchanged(
            ExtendedGameTestHelper helper,
            StoneFamilyCatalog published,
            long publishedVersion,
            ExternalLooseRockBlockEntity placed
    ) {
        helper.assertTrue(
                StoneFamilyCatalog.get() == published,
                "failed resource reload replaced the catalog snapshot"
        );
        helper.assertValueEqual(
                publishedVersion,
                StoneFamilyCatalog.version(),
                "failed resource reload changed the catalog version"
        );
        helper.assertValueEqual(
                MaterialProgressionGameTestMod.EXTERNAL_ROCK.get(),
                StoneFamilyCatalog.get().byId(FAMILY_ID).orElseThrow()
                        .rockItem(),
                "failed resource reload changed the external Rock mapping"
        );
        helper.assertBlockPresent(
                ModBlocks.EXTERNAL_LOOSE_ROCKS.get(),
                ROOT.above()
        );
        helper.assertTrue(
                helper.getBlockEntity(
                        ROOT.above(),
                        ExternalLooseRockBlockEntity.class
                ) == placed,
                "failed resource reload replaced the external Rock entity"
        );
        helper.assertValueEqual(
                FAMILY_ID,
                placed.familyId().orElseThrow(),
                "failed resource reload changed the placed family ID"
        );
        GameTestSupport.assertStack(
                helper,
                placed.rock(),
                MaterialProgressionGameTestMod.EXTERNAL_ROCK.get(),
                1,
                "Rock stored across failed resource reload"
        );
        helper.assertValueEqual(
                0,
                itemCount(
                        helper,
                        ROOT.above(),
                        MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()
                ),
                "old Rock drop after failed resource reload"
        );
        helper.assertValueEqual(
                0,
                itemCount(
                        helper,
                        ROOT.above(),
                        MaterialProgressionGameTestMod
                                .EXTERNAL_ROCK_ALTERNATE.get()
                ),
                "alternate Rock drop after failed resource reload"
        );
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(
                Registries.BLOCK,
                Identifier.fromNamespaceAndPath(
                        MaterialProgressionGameTestMod.MOD_ID,
                        path
                )
        );
    }

    private static ExternalLooseRockBlockEntity assertExternalIdentity(
            ExtendedGameTestHelper helper,
            BlockPos pos
    ) {
        return assertExternalIdentity(
                helper,
                pos,
                MaterialProgressionGameTestMod.EXTERNAL_ROCK.get()
        );
    }

    private static ExternalLooseRockBlockEntity assertExternalIdentity(
            ExtendedGameTestHelper helper,
            BlockPos pos,
            Item expectedRock
    ) {
        helper.assertBlockPresent(ModBlocks.EXTERNAL_LOOSE_ROCKS.get(), pos);
        BlockEntity blockEntity = helper.getBlockEntity(
                pos,
                ExternalLooseRockBlockEntity.class
        );
        helper.assertTrue(
                blockEntity instanceof ExternalLooseRockBlockEntity,
                "external loose-rock block entity missing"
        );
        ExternalLooseRockBlockEntity rocks =
                (ExternalLooseRockBlockEntity) blockEntity;
        helper.assertValueEqual(
                FAMILY_ID,
                rocks.familyId().orElseThrow(),
                "stored external family"
        );
        GameTestSupport.assertStack(
                helper,
                rocks.rock(),
                expectedRock,
                1,
                "stored external Rock"
        );
        return rocks;
    }

    private static void assertCrafts(
            ExtendedGameTestHelper helper,
            List<ItemStack> stacks,
            Item expected
    ) {
        CraftingInput input = CraftingInput.of(2, 2, stacks);
        var recipe = helper.getLevel()
                .getServer()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .orElseThrow();
        GameTestSupport.assertStack(
                helper,
                recipe.value().assemble(input),
                expected,
                1,
                "external cobbling result"
        );
    }

    private static ItemStack externalRock() {
        return new ItemStack(MaterialProgressionGameTestMod.EXTERNAL_ROCK.get());
    }

    private static void enchant(
            ExtendedGameTestHelper helper,
            ItemStack stack,
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key
    ) {
        var enchantment = helper.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
        stack.enchant(enchantment, 1);
    }

    private static boolean placeFeature(
            ExtendedGameTestHelper helper,
            BlockPos position
    ) {
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                Identifier.fromNamespaceAndPath(
                        MaterialProgression.MOD_ID,
                        "loose_rocks"
                )
        );
        ConfiguredFeature<?, ?> feature = helper.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(key)
                .orElseThrow()
                .value();
        return feature.place(
                helper.getLevel(),
                helper.getLevel().getChunkSource().getGenerator(),
                RandomSource.create(42L),
                helper.absolutePos(position)
        );
    }

    private static int itemCount(
            ExtendedGameTestHelper helper,
            BlockPos pos,
            Item item
    ) {
        BlockPos absolute = helper.absolutePos(pos);
        return helper.getLevel()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        AABB.ofSize(Vec3.atCenterOf(absolute), 4.0, 4.0, 4.0)
                )
                .stream()
                .filter(entity -> entity.getItem().is(item))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    private static int allItemCount(
            ExtendedGameTestHelper helper,
            BlockPos pos
    ) {
        BlockPos absolute = helper.absolutePos(pos);
        return helper.getLevel()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        AABB.ofSize(Vec3.atCenterOf(absolute), 4.0, 4.0, 4.0)
                )
                .stream()
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    private static void clearNearbyItems(
            ExtendedGameTestHelper helper,
            BlockPos pos
    ) {
        BlockPos absolute = helper.absolutePos(pos);
        helper.getLevel()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        AABB.ofSize(
                                Vec3.atCenterOf(absolute),
                                8.0,
                                8.0,
                                8.0
                        )
                )
                .forEach(ItemEntity::discard);
    }
}
