package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.progression.LogHarvestRule;
import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class LogHarvestRuleGameTests {
    private LogHarvestRuleGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The log harvest policy preserves its narrow boundary")
    static void logHarvestPolicy(ExtendedGameTestHelper helper) {
        helper.assertFalse(
                LogHarvestRule.canHarvest(
                        true,
                        true,
                        Blocks.OAK_LOG.defaultBlockState(),
                        ItemStack.EMPTY
                ),
                "Enabled rule allowed an empty hand to harvest an oak log"
        );
        helper.assertTrue(
                LogHarvestRule.canHarvest(
                        true,
                        true,
                        Blocks.OAK_LOG.defaultBlockState(),
                        ModItems.FLINT_HATCHET.get().getDefaultInstance()
                ),
                "Enabled rule rejected the tagged flint hatchet"
        );
        helper.assertTrue(
                LogHarvestRule.canHarvest(
                        true,
                        false,
                        Blocks.OAK_LOG.defaultBlockState(),
                        ItemStack.EMPTY
                ),
                "Disabled rule changed vanilla log harvesting"
        );
        helper.assertTrue(
                LogHarvestRule.canHarvest(
                        true,
                        true,
                        Blocks.OAK_PLANKS.defaultBlockState(),
                        ItemStack.EMPTY
                ),
                "Enabled rule affected a non-log wooden block"
        );
        helper.assertFalse(
                LogHarvestRule.canHarvest(
                        true,
                        true,
                        Blocks.CRIMSON_STEM.defaultBlockState(),
                        new ItemStack(Items.STICK)
                ),
                "Enabled rule ignored a non-overworld log-tag member"
        );
        helper.assertFalse(
                LogHarvestRule.canHarvest(
                        false,
                        true,
                        Blocks.OAK_LOG.defaultBlockState(),
                        ModItems.FLINT_HATCHET.get().getDefaultInstance()
                ),
                "Rule upgraded a harvest denied by Minecraft or another mod"
        );
        helper.succeed();
    }
}
