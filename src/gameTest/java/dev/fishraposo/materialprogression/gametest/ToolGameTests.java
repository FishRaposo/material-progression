package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class ToolGameTests {
    private ToolGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tool durability, repair, mining, and enchantment tags are loaded")
    static void toolAndTagContracts(ExtendedGameTestHelper helper) {
        ItemStack tinPickaxe = ModItems.TIN_PICKAXE.get().getDefaultInstance();
        ItemStack bronzePickaxe = ModItems.BRONZE_PICKAXE.get().getDefaultInstance();
        ItemStack tinSword = ModItems.TIN_SWORD.get().getDefaultInstance();
        ItemStack bronzeSword = ModItems.BRONZE_SWORD.get().getDefaultInstance();

        helper.assertTrue(tinPickaxe.getMaxDamage() == 96, "Tin durability changed");
        helper.assertTrue(bronzePickaxe.getMaxDamage() == 325, "Bronze durability changed");
        helper.assertTrue(
                ModItems.TIN_INGOT.get().getDefaultInstance().is(ModTags.INGOTS_TIN),
                "Tin ingots do not repair tin tools"
        );
        helper.assertTrue(
                ModItems.BRONZE_INGOT.get().getDefaultInstance().is(ModTags.INGOTS_BRONZE),
                "Bronze ingots do not repair bronze tools"
        );
        helper.assertTrue(
                tinPickaxe.is(ItemTags.MINING_ENCHANTABLE),
                "Tin pickaxe is not mining-enchantable"
        );
        helper.assertTrue(
                bronzePickaxe.is(ItemTags.DURABILITY_ENCHANTABLE),
                "Bronze pickaxe is not durability-enchantable"
        );
        helper.assertTrue(
                tinSword.is(ItemTags.WEAPON_ENCHANTABLE),
                "Tin sword is not weapon-enchantable"
        );
        helper.assertTrue(
                bronzeSword.is(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                "Bronze sword is not sharp-weapon-enchantable"
        );
        helper.assertTrue(
                ModBlocks.TIN_ORE.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE),
                "Tin ore is not mineable with a pickaxe"
        );
        helper.assertTrue(
                ModItems.FLINT_HAMMER.get().getDefaultInstance().is(ItemTags.PICKAXES),
                "Flint hammer is not a pickaxe-compatible field tool"
        );
        helper.assertTrue(
                ModItems.FLINT_SAW.get().getDefaultInstance().is(ItemTags.AXES),
                "Flint saw is not an axe-compatible field tool"
        );
        helper.assertTrue(
                ModBlocks.TIN_ORE.get().defaultBlockState().is(BlockTags.NEEDS_STONE_TOOL),
                "Tin ore does not require a stone-tier tool"
        );
        helper.assertTrue(
                Blocks.DIAMOND_ORE.defaultBlockState().is(ModTags.INCORRECT_FOR_TIN_TOOL),
                "Tin tools unexpectedly harvest diamond ore"
        );
        helper.assertTrue(
                Blocks.OBSIDIAN.defaultBlockState().is(ModTags.INCORRECT_FOR_BRONZE_TOOL),
                "Bronze tools unexpectedly harvest obsidian"
        );
        helper.succeed();
    }
}
