package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MaterialProgression.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, MaterialProgression.MOD_ID);

    // Provisional MVP balance: tin is deliberately below stone; bronze is an iron-like upgrade.
    public static final ToolMaterial FLINT = new ToolMaterial(
            ModTags.INCORRECT_FOR_FLINT_TOOL,
            64,
            5.0F,
            1.5F,
            5,
            ModTags.FLINT_SHARDS
    );

    public static final ToolMaterial TIN = new ToolMaterial(
            ModTags.INCORRECT_FOR_TIN_TOOL,
            96,
            3.5F,
            0.5F,
            8,
            ModTags.INGOTS_TIN
    );

    public static final ToolMaterial BRONZE = new ToolMaterial(
            ModTags.INCORRECT_FOR_BRONZE_TOOL,
            325,
            6.5F,
            2.0F,
            12,
            ModTags.INGOTS_BRONZE
    );

    public static final DeferredItem<Item> RAW_TIN = ITEMS.registerSimpleItem("raw_tin");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> TIN_DUST = ITEMS.registerSimpleItem("tin_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> BRONZE_DUST = ITEMS.registerSimpleItem("bronze_dust");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> ROCK = ITEMS.registerSimpleItem("rock");
    public static final DeferredItem<Item> GRANITE_ROCK = ITEMS.registerSimpleItem("granite_rock");
    public static final DeferredItem<Item> DIORITE_ROCK = ITEMS.registerSimpleItem("diorite_rock");
    public static final DeferredItem<Item> ANDESITE_ROCK = ITEMS.registerSimpleItem("andesite_rock");
    public static final DeferredItem<Item> DEEPSLATE_ROCK = ITEMS.registerSimpleItem("deepslate_rock");
    public static final DeferredItem<Item> TUFF_ROCK = ITEMS.registerSimpleItem("tuff_rock");
    public static final DeferredItem<Item> CALCITE_ROCK = ITEMS.registerSimpleItem("calcite_rock");
    public static final DeferredItem<Item> DRIPSTONE_ROCK = ITEMS.registerSimpleItem("dripstone_rock");
    public static final DeferredItem<Item> SULFUR_ROCK = ITEMS.registerSimpleItem("sulfur_rock");
    public static final DeferredItem<Item> CINNABAR_ROCK = ITEMS.registerSimpleItem("cinnabar_rock");
    public static final DeferredItem<Item> SANDSTONE_ROCK = ITEMS.registerSimpleItem("sandstone_rock");
    public static final DeferredItem<Item> RED_SANDSTONE_ROCK = ITEMS.registerSimpleItem("red_sandstone_rock");
    public static final DeferredItem<Item> NETHERRACK_ROCK = ITEMS.registerSimpleItem("netherrack_rock");
    public static final DeferredItem<Item> BASALT_ROCK = ITEMS.registerSimpleItem("basalt_rock");
    public static final DeferredItem<Item> BLACKSTONE_ROCK = ITEMS.registerSimpleItem("blackstone_rock");
    public static final DeferredItem<Item> END_STONE_ROCK = ITEMS.registerSimpleItem("end_stone_rock");
    public static final DeferredItem<Item> FLINT_SHARD = ITEMS.registerSimpleItem("flint_shard");

    public static final DeferredItem<AxeItem> FLINT_HATCHET = ITEMS.registerItem(
            "flint_hatchet", properties -> new AxeItem(FLINT, 5.0F, -3.2F, properties)
    );

    public static final DeferredItem<Item> TIN_SWORD = ITEMS.registerItem(
            "tin_sword", properties -> new Item(properties.sword(TIN, 3.0F, -2.4F))
    );
    public static final DeferredItem<Item> TIN_PICKAXE = ITEMS.registerItem(
            "tin_pickaxe", properties -> new Item(properties.pickaxe(TIN, 1.0F, -2.8F))
    );
    public static final DeferredItem<AxeItem> TIN_AXE = ITEMS.registerItem(
            "tin_axe", properties -> new AxeItem(TIN, 6.0F, -3.2F, properties)
    );
    public static final DeferredItem<ShovelItem> TIN_SHOVEL = ITEMS.registerItem(
            "tin_shovel", properties -> new ShovelItem(TIN, 1.5F, -3.0F, properties)
    );
    public static final DeferredItem<HoeItem> TIN_HOE = ITEMS.registerItem(
            "tin_hoe", properties -> new HoeItem(TIN, -1.0F, -2.0F, properties)
    );

    public static final DeferredItem<Item> BRONZE_SWORD = ITEMS.registerItem(
            "bronze_sword", properties -> new Item(properties.sword(BRONZE, 3.0F, -2.4F))
    );
    public static final DeferredItem<Item> BRONZE_PICKAXE = ITEMS.registerItem(
            "bronze_pickaxe", properties -> new Item(properties.pickaxe(BRONZE, 1.0F, -2.8F))
    );
    public static final DeferredItem<AxeItem> BRONZE_AXE = ITEMS.registerItem(
            "bronze_axe", properties -> new AxeItem(BRONZE, 6.0F, -3.1F, properties)
    );
    public static final DeferredItem<ShovelItem> BRONZE_SHOVEL = ITEMS.registerItem(
            "bronze_shovel", properties -> new ShovelItem(BRONZE, 1.5F, -3.0F, properties)
    );
    public static final DeferredItem<HoeItem> BRONZE_HOE = ITEMS.registerItem(
            "bronze_hoe", properties -> new HoeItem(BRONZE, -2.0F, -1.0F, properties)
    );

    public static final DeferredItem<?> CRUSHER = ITEMS.registerSimpleBlockItem("crusher", ModBlocks.CRUSHER);
    public static final DeferredItem<?> TIN_ORE = ITEMS.registerSimpleBlockItem("tin_ore", ModBlocks.TIN_ORE);
    public static final DeferredItem<?> DEEPSLATE_TIN_ORE =
            ITEMS.registerSimpleBlockItem("deepslate_tin_ore", ModBlocks.DEEPSLATE_TIN_ORE);
    public static final DeferredItem<?> COBBLED_GRANITE = blockItem("cobbled_granite", ModBlocks.COBBLED_GRANITE);
    public static final DeferredItem<?> COBBLED_DIORITE = blockItem("cobbled_diorite", ModBlocks.COBBLED_DIORITE);
    public static final DeferredItem<?> COBBLED_ANDESITE = blockItem("cobbled_andesite", ModBlocks.COBBLED_ANDESITE);
    public static final DeferredItem<?> COBBLED_TUFF = blockItem("cobbled_tuff", ModBlocks.COBBLED_TUFF);
    public static final DeferredItem<?> COBBLED_CALCITE = blockItem("cobbled_calcite", ModBlocks.COBBLED_CALCITE);
    public static final DeferredItem<?> COBBLED_DRIPSTONE = blockItem("cobbled_dripstone", ModBlocks.COBBLED_DRIPSTONE);
    public static final DeferredItem<?> COBBLED_SULFUR = blockItem("cobbled_sulfur", ModBlocks.COBBLED_SULFUR);
    public static final DeferredItem<?> COBBLED_CINNABAR = blockItem("cobbled_cinnabar", ModBlocks.COBBLED_CINNABAR);
    public static final DeferredItem<?> COBBLED_SANDSTONE = blockItem("cobbled_sandstone", ModBlocks.COBBLED_SANDSTONE);
    public static final DeferredItem<?> COBBLED_RED_SANDSTONE = blockItem("cobbled_red_sandstone", ModBlocks.COBBLED_RED_SANDSTONE);
    public static final DeferredItem<?> COBBLED_NETHERRACK = blockItem("cobbled_netherrack", ModBlocks.COBBLED_NETHERRACK);
    public static final DeferredItem<?> COBBLED_BASALT = blockItem("cobbled_basalt", ModBlocks.COBBLED_BASALT);
    public static final DeferredItem<?> COBBLED_BLACKSTONE = blockItem("cobbled_blackstone", ModBlocks.COBBLED_BLACKSTONE);
    public static final DeferredItem<?> COBBLED_END_STONE = blockItem("cobbled_end_stone", ModBlocks.COBBLED_END_STONE);

    private static final List<DeferredItem<? extends Item>> MATERIAL_ITEMS = List.of(
            RAW_TIN, TIN_INGOT, TIN_DUST, COPPER_DUST, BRONZE_DUST, BRONZE_INGOT,
            ROCK, GRANITE_ROCK, DIORITE_ROCK, ANDESITE_ROCK, DEEPSLATE_ROCK,
            TUFF_ROCK, CALCITE_ROCK, DRIPSTONE_ROCK, SULFUR_ROCK,
            CINNABAR_ROCK, SANDSTONE_ROCK, RED_SANDSTONE_ROCK,
            NETHERRACK_ROCK, BASALT_ROCK, BLACKSTONE_ROCK, END_STONE_ROCK,
            FLINT_SHARD, FLINT_HATCHET,
            TIN_SWORD, TIN_PICKAXE, TIN_AXE, TIN_SHOVEL, TIN_HOE,
            BRONZE_SWORD, BRONZE_PICKAXE, BRONZE_AXE, BRONZE_SHOVEL, BRONZE_HOE
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.material_progression"))
                    .icon(() -> BRONZE_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CRUSHER.get());
                        output.accept(TIN_ORE.get());
                        output.accept(DEEPSLATE_TIN_ORE.get());
                        output.accept(COBBLED_GRANITE.get());
                        output.accept(COBBLED_DIORITE.get());
                        output.accept(COBBLED_ANDESITE.get());
                        output.accept(COBBLED_TUFF.get());
                        output.accept(COBBLED_CALCITE.get());
                        output.accept(COBBLED_DRIPSTONE.get());
                        output.accept(COBBLED_SULFUR.get());
                        output.accept(COBBLED_CINNABAR.get());
                        output.accept(COBBLED_SANDSTONE.get());
                        output.accept(COBBLED_RED_SANDSTONE.get());
                        output.accept(COBBLED_NETHERRACK.get());
                        output.accept(COBBLED_BASALT.get());
                        output.accept(COBBLED_BLACKSTONE.get());
                        output.accept(COBBLED_END_STONE.get());
                        MATERIAL_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private ModItems() {
    }

    private static DeferredItem<?> blockItem(
            String name, net.neoforged.neoforge.registries.DeferredBlock<?> block
    ) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
