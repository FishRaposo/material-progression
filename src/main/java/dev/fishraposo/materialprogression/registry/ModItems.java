package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
    public static final ToolMaterial COPPER = ToolMaterial.COPPER;
    public static final ToolMaterial ZINC = industrialTool(1, 210, 5.5F, 1.5F, 10, "zinc");
    public static final ToolMaterial LEAD = industrialTool(1, 440, 3.5F, 1.0F, 5, "lead");
    public static final ToolMaterial STEEL = industrialTool(2, 720, 7.0F, 3.0F, 10, "steel");
    public static final ToolMaterial BRASS = industrialTool(2, 380, 6.5F, 2.0F, 15, "brass");
    public static final ToolMaterial NICKEL = industrialTool(2, 820, 6.8F, 2.5F, 10, "nickel");
    public static final ToolMaterial INVAR = industrialTool(2, 1200, 6.2F, 2.8F, 8, "invar");
    public static final ToolMaterial SILVER = industrialTool(1, 250, 6.0F, 1.5F, 18, "silver");
    public static final ToolMaterial ROSE_GOLD = industrialTool(2, 340, 7.0F, 2.0F, 20, "rose_gold");

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
    public static final DeferredItem<Item> RAW_ZINC = tooltipItem("raw_zinc", "tooltip.material_progression.raw_zinc");
    public static final DeferredItem<Item> RAW_LEAD = tooltipItem("raw_lead", "tooltip.material_progression.raw_lead");
    public static final DeferredItem<Item> RAW_NICKEL = tooltipItem("raw_nickel", "tooltip.material_progression.raw_nickel");
    public static final DeferredItem<Item> RAW_SILVER = tooltipItem("raw_silver", "tooltip.material_progression.raw_silver");
    public static final DeferredItem<Item> ZINC_DUST = tooltipItem("zinc_dust", "tooltip.material_progression.zinc_dust");
    public static final DeferredItem<Item> LEAD_DUST = tooltipItem("lead_dust", "tooltip.material_progression.lead_dust");
    public static final DeferredItem<Item> NICKEL_DUST = tooltipItem("nickel_dust", "tooltip.material_progression.nickel_dust");
    public static final DeferredItem<Item> SILVER_DUST = tooltipItem("silver_dust", "tooltip.material_progression.silver_dust");
    public static final DeferredItem<Item> STEEL_DUST = tooltipItem("steel_dust", "tooltip.material_progression.steel_dust");
    public static final DeferredItem<Item> BRASS_DUST = tooltipItem("brass_dust", "tooltip.material_progression.brass_dust");
    public static final DeferredItem<Item> INVAR_DUST = tooltipItem("invar_dust", "tooltip.material_progression.invar_dust");
    public static final DeferredItem<Item> ROSE_GOLD_DUST = tooltipItem("rose_gold_dust", "tooltip.material_progression.rose_gold_dust");
    public static final DeferredItem<Item> ZINC_INGOT = tooltipItem("zinc_ingot", "tooltip.material_progression.zinc_ingot");
    public static final DeferredItem<Item> LEAD_INGOT = tooltipItem("lead_ingot", "tooltip.material_progression.lead_ingot");
    public static final DeferredItem<Item> NICKEL_INGOT = tooltipItem("nickel_ingot", "tooltip.material_progression.nickel_ingot");
    public static final DeferredItem<Item> SILVER_INGOT = tooltipItem("silver_ingot", "tooltip.material_progression.silver_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = tooltipItem("steel_ingot", "tooltip.material_progression.steel_ingot");
    public static final DeferredItem<Item> BRASS_INGOT = tooltipItem("brass_ingot", "tooltip.material_progression.brass_ingot");
    public static final DeferredItem<Item> INVAR_INGOT = tooltipItem("invar_ingot", "tooltip.material_progression.invar_ingot");
    public static final DeferredItem<Item> ROSE_GOLD_INGOT = tooltipItem("rose_gold_ingot", "tooltip.material_progression.rose_gold_ingot");
    public static final DeferredItem<Item> SULFUR_DUST = tooltipItem("sulfur_dust", "tooltip.material_progression.sulfur_dust");
    public static final DeferredItem<Item> COAL_DUST = tooltipItem("coal_dust", "tooltip.material_progression.coal_dust");
    public static final DeferredItem<Item> SULFUR_COKE_DUST = tooltipItem("sulfur_coke_dust", "tooltip.material_progression.sulfur_coke_dust");
    public static final DeferredItem<Item> SULFUR_COKE = tooltipItem(
            "sulfur_coke", "tooltip.material_progression.sulfur_coke"
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
    public static final DeferredItem<AxeItem> BRONZE_HATCHET = ITEMS.registerItem(
            "bronze_hatchet",
            properties -> new AxeItem(
                    BRONZE,
                    5.0F,
                    -3.0F,
                    withTooltip(properties, "tooltip.material_progression.hatchet")
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

    private static final List<DeferredItem<? extends Item>> MATERIAL_ITEMS = new ArrayList<>(List.of(
            RAW_TIN, TIN_INGOT, TIN_DUST, COPPER_DUST, BRONZE_DUST, BRONZE_INGOT,
            RAW_ZINC, RAW_LEAD, RAW_NICKEL, RAW_SILVER,
            ZINC_DUST, LEAD_DUST, NICKEL_DUST, SILVER_DUST, STEEL_DUST,
            BRASS_DUST, INVAR_DUST, ROSE_GOLD_DUST,
            ZINC_INGOT, LEAD_INGOT, NICKEL_INGOT, SILVER_INGOT, STEEL_INGOT,
            BRASS_INGOT, INVAR_INGOT, ROSE_GOLD_INGOT,
            SULFUR_DUST, COAL_DUST, SULFUR_COKE_DUST, SULFUR_COKE,
            PLANT_FIBER,
            ROCK, GRANITE_ROCK, DIORITE_ROCK, ANDESITE_ROCK, DEEPSLATE_ROCK,
            TUFF_ROCK, CALCITE_ROCK, DRIPSTONE_ROCK, SULFUR_ROCK,
            CINNABAR_ROCK, SANDSTONE_ROCK, RED_SANDSTONE_ROCK,
            NETHERRACK_ROCK, BASALT_ROCK, BLACKSTONE_ROCK, END_STONE_ROCK,
            FLINT_SHARD, FLINT_HATCHET, FLINT_KNIFE, FLINT_HAMMER, FLINT_SAW,
            TIN_SWORD, TIN_PICKAXE, TIN_AXE, TIN_SHOVEL, TIN_HOE,
            BRONZE_SWORD, BRONZE_PICKAXE, BRONZE_AXE, BRONZE_SHOVEL, BRONZE_HOE,
            BRONZE_KNIFE, BRONZE_HAMMER, BRONZE_SAW, BRONZE_HATCHET
    ));
    private static final Map<String, DeferredItem<? extends Item>> INDUSTRIAL_TOOLS =
            new LinkedHashMap<>();
    private static final Map<String, DeferredItem<? extends Item>> INDUSTRIAL_ARMOR =
            new LinkedHashMap<>();
    private static final Map<String, DeferredItem<? extends Item>> ORE_BLOCK_ITEMS =
            new LinkedHashMap<>();
    private static final Map<String, ToolMaterial> TOOL_MATERIALS = toolMaterials();
    private static final Map<String, ArmorMaterial> ARMOR_MATERIALS = armorMaterials();

    static {
        // Vanilla already supplies the five standard Wood and Stone tools; this
        // mod supplies their progression-specific field-tool complements.
        registerTools("wood", ToolMaterial.WOOD, List.of("knife", "hammer", "saw", "hatchet"));
        registerTools("stone", ToolMaterial.STONE, List.of("knife", "hammer", "saw", "hatchet"));
        registerTools("flint", FLINT, List.of("sword", "pickaxe", "axe", "shovel", "hoe"));
        registerTools("tin", TIN, List.of("knife", "hammer", "saw", "hatchet"));
        for (String material : List.of("copper", "zinc", "lead", "steel", "brass", "nickel", "invar", "silver", "rose_gold")) {
            registerTools(material, TOOL_MATERIALS.get(material), List.of(
                    "sword", "pickaxe", "axe", "shovel", "hoe", "knife", "hammer", "saw", "hatchet"
            ));
        }
        for (String material : ModMaterials.EQUIPMENT_MATERIALS) {
            registerArmor(material, ARMOR_MATERIALS.get(material));
        }
        for (Map.Entry<String, net.neoforged.neoforge.registries.DeferredBlock<Block>> entry : ModBlocks.oreBlocks()) {
            String id = entry.getKey();
            if (id.equals("tin_ore") || id.equals("deepslate_tin_ore")) {
                continue;
            }
            ORE_BLOCK_ITEMS.put(id, blockItem(id, entry.getValue(), "tooltip.material_progression.ore"));
        }
        MATERIAL_ITEMS.addAll(INDUSTRIAL_TOOLS.values());
        MATERIAL_ITEMS.addAll(INDUSTRIAL_ARMOR.values());
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.material_progression"))
                    .icon(() -> BRONZE_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CRUSHER.get());
                        output.accept(MANUAL_WORKSHOP.get());
                        output.accept(TIN_ORE.get());
                        output.accept(DEEPSLATE_TIN_ORE.get());
                        ORE_BLOCK_ITEMS.values().forEach(item -> output.accept(item.get()));
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

    private static Map<String, ToolMaterial> toolMaterials() {
        Map<String, ToolMaterial> tools = new LinkedHashMap<>();
        tools.put("copper", COPPER);
        tools.put("zinc", ZINC);
        tools.put("lead", LEAD);
        tools.put("steel", STEEL);
        tools.put("brass", BRASS);
        tools.put("nickel", NICKEL);
        tools.put("invar", INVAR);
        tools.put("silver", SILVER);
        tools.put("rose_gold", ROSE_GOLD);
        return Map.copyOf(tools);
    }

    private static ToolMaterial industrialTool(
            int level, int durability, float speed, float attack,
            int enchantment, String repairMaterial
    ) {
        return new ToolMaterial(
                level >= 2 ? ModTags.INCORRECT_FOR_BRONZE_TOOL : ModTags.INCORRECT_FOR_TIN_TOOL,
                durability, speed, attack, enchantment, ModTags.ingot(repairMaterial)
        );
    }

    private static void registerTools(String material, ToolMaterial toolMaterial, List<String> roles) {
        for (String role : roles) {
            String id = material + "_" + role;
            DeferredItem<? extends Item> tool = switch (role) {
                case "axe", "hatchet", "saw" -> ITEMS.registerItem(id, properties ->
                        new AxeItem(toolMaterial, role.equals("saw") ? 4.0F : 6.0F,
                                role.equals("saw") ? -2.8F : -3.1F,
                                withTooltip(properties, tooltipForRole(role))));
                case "shovel" -> ITEMS.registerItem(id, properties ->
                        new ShovelItem(toolMaterial, 1.5F, -3.0F,
                                withTooltip(properties, tooltipForRole(role))));
                case "hoe" -> ITEMS.registerItem(id, properties ->
                        new HoeItem(toolMaterial, -1.0F, -2.0F,
                                withTooltip(properties, tooltipForRole(role))));
                case "pickaxe", "hammer" -> ITEMS.registerItem(id, properties -> new Item(
                        withTooltip(properties, tooltipForRole(role))
                                .pickaxe(toolMaterial, role.equals("hammer") ? 2.0F : 1.0F,
                                        role.equals("hammer") ? -3.0F : -2.8F)));
                default -> ITEMS.registerItem(id, properties -> new Item(
                        withTooltip(properties, tooltipForRole(role))
                                .sword(toolMaterial, role.equals("knife") ? 1.0F : 3.0F,
                                        role.equals("knife") ? -1.8F : -2.4F)));
            };
            INDUSTRIAL_TOOLS.put(id, tool);
        }
    }

    private static String tooltipForRole(String role) {
        return switch (role) {
            case "axe" -> "tooltip.material_progression.axe";
            case "hatchet" -> "tooltip.material_progression.hatchet";
            case "saw" -> "tooltip.material_progression.saw";
            case "shovel" -> "tooltip.material_progression.shovel";
            case "hoe" -> "tooltip.material_progression.hoe";
            case "pickaxe" -> "tooltip.material_progression.pickaxe";
            case "hammer" -> "tooltip.material_progression.hammer";
            case "knife" -> "tooltip.material_progression.knife";
            case "sword" -> "tooltip.material_progression.sword";
            default -> "tooltip.material_progression.tool";
        };
    }

    private static Map<String, ArmorMaterial> armorMaterials() {
        Map<String, ArmorMaterial> materials = new LinkedHashMap<>();
        for (String material : ModMaterials.EQUIPMENT_MATERIALS) {
            int durability = ModMaterials.material(material).orElseThrow().durability();
            int defense = switch (material) {
                case "wood", "flint" -> 1;
                case "stone", "lead" -> 3;
                case "bronze", "steel", "nickel", "invar", "brass", "rose_gold" -> 3;
                default -> 2;
            };
            float toughness = switch (material) {
                case "steel", "nickel" -> 1.0F;
                case "invar" -> 2.0F;
                default -> 0.0F;
            };
            float knockback = switch (material) {
                case "stone" -> 0.05F;
                case "lead" -> 0.15F;
                case "invar" -> 0.10F;
                default -> 0.0F;
            };
            materials.put(material, armorMaterial(material, durability, defense, toughness, knockback));
        }
        return Map.copyOf(materials);
    }

    private static ArmorMaterial armorMaterial(
            String material, int durability, int defense,
            float toughness, float knockback
    ) {
        Map<ArmorType, Integer> defenses = new EnumMap<>(ArmorType.class);
        defenses.put(ArmorType.HELMET, defense);
        defenses.put(ArmorType.CHESTPLATE, defense + 2);
        defenses.put(ArmorType.LEGGINGS, defense + 2);
        defenses.put(ArmorType.BOOTS, defense);
        ResourceKey<EquipmentAsset> asset = ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                Identifier.fromNamespaceAndPath(MaterialProgression.MOD_ID, material)
        );
        return new ArmorMaterial(
                Math.max(5, durability / 12), defenses, 10,
                SoundEvents.ARMOR_EQUIP_IRON, toughness, knockback,
                repairTag(material), asset
        );
    }

    private static net.minecraft.tags.TagKey<Item> repairTag(String material) {
        return switch (material) {
            case "wood" -> net.minecraft.tags.ItemTags.PLANKS;
            case "stone" -> ModTags.commonItemTagForPublicUse("cobblestones");
            case "flint" -> ModTags.FLINT_SHARDS;
            case "copper" -> ModTags.ingot("copper");
            default -> ModTags.ingot(material);
        };
    }

    private static void registerArmor(String material, ArmorMaterial armorMaterial) {
        for (ArmorType type : List.of(ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS)) {
            String id = material + "_" + type.getName();
            INDUSTRIAL_ARMOR.put(id, ITEMS.registerItem(id, properties -> new Item(
                    withTooltip(properties, "tooltip.material_progression.armor")
                            .humanoidArmor(armorMaterial, type)
            )));
        }
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
