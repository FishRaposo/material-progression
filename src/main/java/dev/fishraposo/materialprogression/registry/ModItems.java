package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
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

    public static final DeferredItem<Item> RAW_TIN = tooltipItem(
            "raw_tin", "tooltip.material_progression.raw_tin"
    );
    public static final DeferredItem<Item> TIN_INGOT = tooltipItem(
            "tin_ingot", "tooltip.material_progression.tin_ingot"
    );
    public static final DeferredItem<Item> TIN_DUST = tooltipItem(
            "tin_dust", "tooltip.material_progression.tin_dust"
    );
    public static final DeferredItem<Item> COPPER_DUST = tooltipItem(
            "copper_dust", "tooltip.material_progression.copper_dust"
    );
    public static final DeferredItem<Item> BRONZE_DUST = tooltipItem(
            "bronze_dust", "tooltip.material_progression.bronze_dust"
    );
    public static final DeferredItem<Item> BRONZE_INGOT = tooltipItem(
            "bronze_ingot", "tooltip.material_progression.bronze_ingot"
    );
    public static final DeferredItem<Item> PLANT_FIBER = tooltipItem(
            "plant_fiber",
            "tooltip.material_progression.plant_fiber"
    );
    public static final DeferredItem<Item> ROCK = rockItem("rock");
    public static final DeferredItem<Item> GRANITE_ROCK = rockItem("granite_rock");
    public static final DeferredItem<Item> DIORITE_ROCK = rockItem("diorite_rock");
    public static final DeferredItem<Item> ANDESITE_ROCK = rockItem("andesite_rock");
    public static final DeferredItem<Item> DEEPSLATE_ROCK = rockItem("deepslate_rock");
    public static final DeferredItem<Item> TUFF_ROCK = rockItem("tuff_rock");
    public static final DeferredItem<Item> CALCITE_ROCK = rockItem("calcite_rock");
    public static final DeferredItem<Item> DRIPSTONE_ROCK = rockItem("dripstone_rock");
    public static final DeferredItem<Item> SULFUR_ROCK = rockItem("sulfur_rock");
    public static final DeferredItem<Item> CINNABAR_ROCK = rockItem("cinnabar_rock");
    public static final DeferredItem<Item> SANDSTONE_ROCK = rockItem("sandstone_rock");
    public static final DeferredItem<Item> RED_SANDSTONE_ROCK = rockItem("red_sandstone_rock");
    public static final DeferredItem<Item> NETHERRACK_ROCK = rockItem("netherrack_rock");
    public static final DeferredItem<Item> BASALT_ROCK = rockItem("basalt_rock");
    public static final DeferredItem<Item> BLACKSTONE_ROCK = rockItem("blackstone_rock");
    public static final DeferredItem<Item> END_STONE_ROCK = rockItem("end_stone_rock");
    public static final DeferredItem<Item> FLINT_SHARD = tooltipItem(
            "flint_shard",
            "tooltip.material_progression.flint_shard"
    );

    public static final DeferredItem<AxeItem> FLINT_HATCHET = ITEMS.registerItem(
            "flint_hatchet",
            properties -> new AxeItem(
                    FLINT,
                    5.0F,
                    -3.2F,
                    withTooltip(
                            properties,
                            "tooltip.material_progression.flint_hatchet"
                    )
            )
    );
    public static final DeferredItem<Item> FLINT_KNIFE = ITEMS.registerItem(
            "flint_knife",
            properties -> new Item(withTooltip(
                    properties,
                    "tooltip.material_progression.knife"
            ).sword(FLINT, 1.0F, -1.8F))
    );
    public static final DeferredItem<Item> FLINT_HAMMER = ITEMS.registerItem(
            "flint_hammer",
            properties -> new Item(withTooltip(
                    properties,
                    "tooltip.material_progression.hammer"
            ).pickaxe(FLINT, 2.0F, -3.0F))
    );
    public static final DeferredItem<AxeItem> FLINT_SAW = ITEMS.registerItem(
            "flint_saw",
            properties -> new AxeItem(
                    FLINT,
                    4.0F,
                    -2.8F,
                    withTooltip(properties, "tooltip.material_progression.saw")
            )
    );

    public static final DeferredItem<Item> TIN_SWORD = ITEMS.registerItem(
            "tin_sword", properties -> new Item(
                    withTooltip(properties, "tooltip.material_progression.tin_tool")
                            .sword(TIN, 3.0F, -2.4F)
            )
    );
    public static final DeferredItem<Item> TIN_PICKAXE = ITEMS.registerItem(
            "tin_pickaxe", properties -> new Item(
                    withTooltip(properties, "tooltip.material_progression.tin_tool")
                            .pickaxe(TIN, 1.0F, -2.8F)
            )
    );
    public static final DeferredItem<AxeItem> TIN_AXE = ITEMS.registerItem(
            "tin_axe", properties -> new AxeItem(
                    TIN,
                    6.0F,
                    -3.2F,
                    withTooltip(properties, "tooltip.material_progression.tin_tool")
            )
    );
    public static final DeferredItem<ShovelItem> TIN_SHOVEL = ITEMS.registerItem(
            "tin_shovel", properties -> new ShovelItem(
                    TIN,
                    1.5F,
                    -3.0F,
                    withTooltip(properties, "tooltip.material_progression.tin_tool")
            )
    );
    public static final DeferredItem<HoeItem> TIN_HOE = ITEMS.registerItem(
            "tin_hoe", properties -> new HoeItem(
                    TIN,
                    -1.0F,
                    -2.0F,
                    withTooltip(properties, "tooltip.material_progression.tin_tool")
            )
    );

    public static final DeferredItem<Item> BRONZE_SWORD = ITEMS.registerItem(
            "bronze_sword", properties -> new Item(
                    withTooltip(properties, "tooltip.material_progression.bronze_tool")
                            .sword(BRONZE, 3.0F, -2.4F)
            )
    );
    public static final DeferredItem<Item> BRONZE_PICKAXE = ITEMS.registerItem(
            "bronze_pickaxe", properties -> new Item(
                    withTooltip(properties, "tooltip.material_progression.bronze_tool")
                            .pickaxe(BRONZE, 1.0F, -2.8F)
            )
    );
    public static final DeferredItem<AxeItem> BRONZE_AXE = ITEMS.registerItem(
            "bronze_axe", properties -> new AxeItem(
                    BRONZE,
                    6.0F,
                    -3.1F,
                    withTooltip(properties, "tooltip.material_progression.bronze_tool")
            )
    );
    public static final DeferredItem<ShovelItem> BRONZE_SHOVEL = ITEMS.registerItem(
            "bronze_shovel", properties -> new ShovelItem(
                    BRONZE,
                    1.5F,
                    -3.0F,
                    withTooltip(properties, "tooltip.material_progression.bronze_tool")
            )
    );
    public static final DeferredItem<HoeItem> BRONZE_HOE = ITEMS.registerItem(
            "bronze_hoe", properties -> new HoeItem(
                    BRONZE,
                    -2.0F,
                    -1.0F,
                    withTooltip(properties, "tooltip.material_progression.bronze_tool")
            )
    );
    public static final DeferredItem<Item> BRONZE_KNIFE = ITEMS.registerItem(
            "bronze_knife",
            properties -> new Item(withTooltip(
                    properties,
                    "tooltip.material_progression.knife"
            ).sword(BRONZE, 1.0F, -1.8F))
    );
    public static final DeferredItem<Item> BRONZE_HAMMER = ITEMS.registerItem(
            "bronze_hammer",
            properties -> new Item(withTooltip(
                    properties,
                    "tooltip.material_progression.hammer"
            ).pickaxe(BRONZE, 2.0F, -3.0F))
    );
    public static final DeferredItem<AxeItem> BRONZE_SAW = ITEMS.registerItem(
            "bronze_saw",
            properties -> new AxeItem(
                    BRONZE,
                    4.0F,
                    -2.8F,
                    withTooltip(properties, "tooltip.material_progression.saw")
            )
    );

    public static final DeferredItem<?> CRUSHER = blockItem(
            "crusher", ModBlocks.CRUSHER, "tooltip.material_progression.crusher"
    );
    public static final DeferredItem<?> MANUAL_WORKSHOP = ITEMS.registerItem(
            "manual_workshop",
            properties -> new BlockItem(
                    ModBlocks.MANUAL_WORKSHOP.get(),
                    withTooltip(
                            properties.useBlockDescriptionPrefix(),
                            "tooltip.material_progression.manual_workshop"
                    )
            )
    );
    public static final DeferredItem<?> TIN_ORE = blockItem(
            "tin_ore", ModBlocks.TIN_ORE, "tooltip.material_progression.tin_ore"
    );
    public static final DeferredItem<?> DEEPSLATE_TIN_ORE = blockItem(
            "deepslate_tin_ore",
            ModBlocks.DEEPSLATE_TIN_ORE,
            "tooltip.material_progression.tin_ore"
    );
    public static final DeferredItem<?> COBBLED_GRANITE = blockItem("cobbled_granite", ModBlocks.COBBLED_GRANITE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_DIORITE = blockItem("cobbled_diorite", ModBlocks.COBBLED_DIORITE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_ANDESITE = blockItem("cobbled_andesite", ModBlocks.COBBLED_ANDESITE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_TUFF = blockItem("cobbled_tuff", ModBlocks.COBBLED_TUFF, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_CALCITE = blockItem("cobbled_calcite", ModBlocks.COBBLED_CALCITE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_DRIPSTONE = blockItem("cobbled_dripstone", ModBlocks.COBBLED_DRIPSTONE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_SULFUR = blockItem("cobbled_sulfur", ModBlocks.COBBLED_SULFUR, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_CINNABAR = blockItem("cobbled_cinnabar", ModBlocks.COBBLED_CINNABAR, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_SANDSTONE = blockItem("cobbled_sandstone", ModBlocks.COBBLED_SANDSTONE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_RED_SANDSTONE = blockItem("cobbled_red_sandstone", ModBlocks.COBBLED_RED_SANDSTONE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_NETHERRACK = blockItem("cobbled_netherrack", ModBlocks.COBBLED_NETHERRACK, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_BASALT = blockItem("cobbled_basalt", ModBlocks.COBBLED_BASALT, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_BLACKSTONE = blockItem("cobbled_blackstone", ModBlocks.COBBLED_BLACKSTONE, "tooltip.material_progression.cobble");
    public static final DeferredItem<?> COBBLED_END_STONE = blockItem("cobbled_end_stone", ModBlocks.COBBLED_END_STONE, "tooltip.material_progression.cobble");

    private static final List<DeferredItem<? extends Item>> MATERIAL_ITEMS = List.of(
            RAW_TIN, TIN_INGOT, TIN_DUST, COPPER_DUST, BRONZE_DUST, BRONZE_INGOT,
            PLANT_FIBER,
            ROCK, GRANITE_ROCK, DIORITE_ROCK, ANDESITE_ROCK, DEEPSLATE_ROCK,
            TUFF_ROCK, CALCITE_ROCK, DRIPSTONE_ROCK, SULFUR_ROCK,
            CINNABAR_ROCK, SANDSTONE_ROCK, RED_SANDSTONE_ROCK,
            NETHERRACK_ROCK, BASALT_ROCK, BLACKSTONE_ROCK, END_STONE_ROCK,
            FLINT_SHARD, FLINT_HATCHET, FLINT_KNIFE, FLINT_HAMMER, FLINT_SAW,
            TIN_SWORD, TIN_PICKAXE, TIN_AXE, TIN_SHOVEL, TIN_HOE,
            BRONZE_SWORD, BRONZE_PICKAXE, BRONZE_AXE, BRONZE_SHOVEL, BRONZE_HOE,
            BRONZE_KNIFE, BRONZE_HAMMER, BRONZE_SAW
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.material_progression"))
                    .icon(() -> BRONZE_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CRUSHER.get());
                        output.accept(MANUAL_WORKSHOP.get());
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

    private static DeferredItem<?> blockItem(
            String name,
            net.neoforged.neoforge.registries.DeferredBlock<?> block,
            String translationKey
    ) {
        return ITEMS.registerItem(
                name,
                properties -> new BlockItem(
                        block.get(),
                        withTooltip(properties.useBlockDescriptionPrefix(), translationKey)
                )
        );
    }

    private static DeferredItem<Item> rockItem(String name) {
        return tooltipItem(name, "tooltip.material_progression.rock");
    }

    private static DeferredItem<Item> tooltipItem(
            String name,
            String translationKey
    ) {
        return ITEMS.registerItem(
                name,
                properties -> new Item(withTooltip(properties, translationKey))
        );
    }

    private static Item.Properties withTooltip(
            Item.Properties properties,
            String translationKey
    ) {
        return properties.component(
                DataComponents.LORE,
                new ItemLore(List.of(Component.translatable(translationKey)))
        );
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
