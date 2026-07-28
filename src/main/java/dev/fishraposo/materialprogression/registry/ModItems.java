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

    private static final List<DeferredItem<? extends Item>> MATERIAL_ITEMS = List.of(
            RAW_TIN, TIN_INGOT, TIN_DUST, COPPER_DUST, BRONZE_DUST, BRONZE_INGOT,
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
                        MATERIAL_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
