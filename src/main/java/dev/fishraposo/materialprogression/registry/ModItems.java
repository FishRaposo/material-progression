package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.data.MaterialFamilies;
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
    public static final ToolMaterial FLINT =
            MaterialFamilies.FLINT.toolMaterial();

    public static final ToolMaterial TIN =
            MaterialFamilies.TIN.toolMaterial();

    public static final ToolMaterial BRONZE =
            MaterialFamilies.BRONZE.toolMaterial();

    public static final DeferredItem<Item> RAW_TIN = ITEMS.registerSimpleItem("raw_tin");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> TIN_DUST = ITEMS.registerSimpleItem("tin_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> BRONZE_DUST = ITEMS.registerSimpleItem("bronze_dust");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> ROCK = ITEMS.registerSimpleItem("rock");
    public static final DeferredItem<Item> FLINT_SHARD = ITEMS.registerSimpleItem("flint_shard");
    public static final DeferredItem<Item> PLANT_FIBER = ITEMS.registerSimpleItem("plant_fiber");

    public static final DeferredItem<AxeItem> FLINT_HATCHET = ITEMS.registerItem(
            "flint_hatchet", properties -> new AxeItem(FLINT, 5.0F, -3.2F, properties)
    );
    public static final DeferredItem<Item> FLINT_KNIFE = ITEMS.registerItem(
            "flint_knife",
            properties -> new Item(properties.sword(FLINT, 2.0F, -2.0F))
    );
    public static final DeferredItem<Item> FLINT_HAMMER = ITEMS.registerItem(
            "flint_hammer",
            properties -> new Item(properties.pickaxe(FLINT, 1.0F, -2.8F))
    );
    public static final DeferredItem<AxeItem> FLINT_SAW = ITEMS.registerItem(
            "flint_saw",
            properties -> new AxeItem(FLINT, 5.0F, -3.1F, properties)
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
    public static final DeferredItem<?> WORKSHOP =
            ITEMS.registerSimpleBlockItem("workshop", ModBlocks.WORKSHOP);
    public static final DeferredItem<?> TIN_ORE = ITEMS.registerSimpleBlockItem("tin_ore", ModBlocks.TIN_ORE);
    public static final DeferredItem<?> DEEPSLATE_TIN_ORE =
            ITEMS.registerSimpleBlockItem("deepslate_tin_ore", ModBlocks.DEEPSLATE_TIN_ORE);
    public static final DeferredItem<?> LOOSE_ROCKS =
            ITEMS.registerSimpleBlockItem("loose_rocks", ModBlocks.LOOSE_ROCKS);
    public static final DeferredItem<?> GROUND_STICK =
            ITEMS.registerSimpleBlockItem("ground_stick", ModBlocks.GROUND_STICK);

    public static List<DeferredItem<? extends Item>> creativeTabContents() {
        return List.of(
            LOOSE_ROCKS, GROUND_STICK,
            ROCK, FLINT_SHARD, FLINT_HATCHET, FLINT_HAMMER, FLINT_KNIFE,
            FLINT_SAW, PLANT_FIBER,
            TIN_ORE, DEEPSLATE_TIN_ORE, RAW_TIN, TIN_DUST, TIN_INGOT,
            TIN_SWORD, TIN_PICKAXE, TIN_AXE, TIN_SHOVEL, TIN_HOE,
            COPPER_DUST, BRONZE_DUST, BRONZE_INGOT,
            BRONZE_SWORD, BRONZE_PICKAXE, BRONZE_AXE, BRONZE_SHOVEL, BRONZE_HOE,
            CRUSHER, WORKSHOP
        );
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.material_progression"))
                    .icon(() -> BRONZE_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) ->
                            creativeTabContents().forEach(item -> output.accept(item.get())))
                    .build());

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
