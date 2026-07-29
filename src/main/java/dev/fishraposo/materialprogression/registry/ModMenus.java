package dev.fishraposo.materialprogression.registry;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.CrusherMenu;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, MaterialProgression.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrusherMenu>> CRUSHER =
            MENUS.register(
                    "crusher",
                    () -> IMenuTypeExtension.create(
                            (containerId, inventory, data) -> new CrusherMenu(containerId, inventory)
                    )
            );

    public static final DeferredHolder<MenuType<?>, MenuType<WorkshopMenu>> WORKSHOP =
            MENUS.register(
                    "workshop",
                    () -> IMenuTypeExtension.create(
                            (containerId, inventory, data) ->
                                    new WorkshopMenu(containerId, inventory)
                    )
            );

    private ModMenus() {
    }

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
