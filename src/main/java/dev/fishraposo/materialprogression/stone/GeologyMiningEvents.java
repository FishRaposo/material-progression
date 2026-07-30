package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public final class GeologyMiningEvents {
    private GeologyMiningEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(GeologyMiningEvents::onHarvestCheck);
        NeoForge.EVENT_BUS.addListener(GeologyMiningEvents::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(GeologyMiningEvents::onBlockDrops);
    }

    private static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (!MaterialProgressionConfig.enableGeologicalHardness()
                || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        GeologyTierResolver.resolve(
                level,
                event.getPos(),
                event.getTargetBlock()
        ).ifPresent(tier -> event.setCanHarvest(
                event.canHarvest()
                        && GeologyToolCapability.canMine(
                                event.getEntity().getMainHandItem(),
                                event.getTargetBlock(),
                                tier
                        )
        ));
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!MaterialProgressionConfig.enableGeologicalHardness()
                || !(event.getEntity().level() instanceof ServerLevel level)
                || event.getPosition().isEmpty()) {
            return;
        }
        GeologyTierResolver.resolve(
                level,
                event.getPosition().orElseThrow(),
                event.getState()
        ).ifPresent(tier ->
                event.setNewSpeed(event.getNewSpeed() / tier.speedDivisor())
        );
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (!MaterialProgressionConfig.enableStoneRockDrops()
                || !(event.getBreaker() instanceof Player player)) {
            return;
        }
        var entry = StoneFamilyCatalog.get().byRaw(event.getState());
        if (entry.isEmpty()) {
            return;
        }

        ItemStack tool = event.getTool();
        var enchantments = event.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);
        if (tool.getEnchantmentLevel(
                enchantments.getOrThrow(Enchantments.SILK_TOUCH)
        ) > 0) {
            return;
        }

        if (MaterialProgressionConfig.enableGeologicalHardness()) {
            var tier = GeologyTierResolver.resolve(
                    event.getLevel(),
                    event.getPos(),
                    event.getState()
            );
            if (tier.isEmpty()
                    || !GeologyToolCapability.canMine(
                            tool,
                            event.getState(),
                            tier.orElseThrow()
                    )) {
                event.getDrops().clear();
                return;
            }
        } else if (!player.hasCorrectToolForDrops(
                event.getState(),
                event.getLevel(),
                event.getPos()
        )) {
            event.getDrops().clear();
            return;
        }

        List<ItemStack> familyRock = Block.getDrops(
                ModBlocks.LOOSE_ROCKS.get()
                        .defaultBlockState()
                        .setValue(
                                dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock.FAMILY,
                                entry.orElseThrow().family()
                        ),
                event.getLevel(),
                event.getPos(),
                null,
                player,
                tool
        );
        if (familyRock.size() != 1 || familyRock.getFirst().isEmpty()) {
            return;
        }

        int fortune = tool.getEnchantmentLevel(
                enchantments.getOrThrow(Enchantments.FORTUNE)
        );
        int count = fortune > 0
                ? 4
                : 2 + event.getLevel().getRandom().nextInt(2);
        ItemStack drop = familyRock.getFirst().copyWithCount(count);
        event.getDrops().clear();
        event.getDrops().add(new ItemEntity(
                event.getLevel(),
                event.getPos().getX() + 0.5,
                event.getPos().getY() + 0.5,
                event.getPos().getZ() + 0.5,
                drop
        ));
    }
}
