package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import dev.fishraposo.materialprogression.stone.StoneResistance;
import dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
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
}
