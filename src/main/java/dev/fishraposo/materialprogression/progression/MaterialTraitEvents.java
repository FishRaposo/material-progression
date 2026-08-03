package dev.fishraposo.materialprogression.progression;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Full matching sets express material identity without adding an RPG system. */
public final class MaterialTraitEvents {
    private static final Map<String, Float> HARDWEARING = Map.of(
            "steel", 0.10F, "nickel", 0.15F, "invar", 0.25F
    );
    private static final Map<String, Float> HEAT_SAFE = Map.of(
            "bronze", 0.25F, "invar", 0.50F
    );

    private MaterialTraitEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(MaterialTraitEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(MaterialTraitEvents::onKnockBack);
        NeoForge.EVENT_BUS.addListener(MaterialTraitEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(MaterialTraitEvents::onArmorHurt);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (wears(player, "brass") || wears(player, "rose_gold")) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 25, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, 25, 0, true, false, false));
        }
        if (wears(player, "stone") || wears(player, "lead")) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25,
                    wears(player, "lead") ? 1 : 0, true, false, false));
        }
    }

    private static void onKnockBack(LivingKnockBackEvent event) {
        if (wears(event.getEntity(), "lead")) {
            event.setStrength(event.getStrength() * 0.55F);
        } else if (wears(event.getEntity(), "stone")) {
            event.setStrength(event.getStrength() * 0.80F);
        }
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(DamageTypeTags.IS_FIRE)) {
            return;
        }
        for (Map.Entry<String, Float> entry : HEAT_SAFE.entrySet()) {
            if (wears(event.getEntity(), entry.getKey())) {
                event.setAmount(event.getAmount() * (1.0F - entry.getValue()));
                return;
            }
        }
    }

    private static void onArmorHurt(ArmorHurtEvent event) {
        for (Map.Entry<String, Float> entry : HARDWEARING.entrySet()) {
            if (!wears(event.getEntity(), entry.getKey())) {
                continue;
            }
            for (EquipmentSlot slot : event.getArmorMap().keySet()) {
                if (event.getEntity().getRandom().nextFloat() < entry.getValue()) {
                    event.setNewDamage(slot, 0.0F);
                }
            }
            return;
        }
    }

    private static boolean wears(net.minecraft.world.entity.LivingEntity entity, String material) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            String id = BuiltInRegistries.ITEM.getKey(entity.getItemBySlot(slot).getItem()).toString();
            if (!id.equals("material_progression:" + material + "_" + slotName(slot))) {
                return false;
            }
        }
        return true;
    }

    private static String slotName(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> throw new IllegalArgumentException("Not an armor slot: " + slot);
        };
    }
}
