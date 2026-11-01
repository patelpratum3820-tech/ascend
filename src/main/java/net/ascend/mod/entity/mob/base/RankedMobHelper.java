package net.ascend.mod.entity.mob.base;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.item.AscendItems;
import net.ascend.mod.network.AscendPackets;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

public class RankedMobHelper {

    public static void applyRank(MobEntity entity, MobRank rank, String baseName) {
        if (entity == null || rank == null) return;

        // 1. Health scaling
        EntityAttributeInstance maxHpAttr = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHpAttr != null) {
            double baseHp = maxHpAttr.getBaseValue();
            maxHpAttr.setBaseValue(baseHp * rank.getHpMultiplier());
            entity.setHealth((float) (baseHp * rank.getHpMultiplier()));
        }

        // 2. Damage scaling
        EntityAttributeInstance dmgAttr = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (dmgAttr != null) {
            double baseDmg = dmgAttr.getBaseValue();
            dmgAttr.setBaseValue(baseDmg * rank.getDamageMultiplier());
        }

        // 3. Name tag with rank color
        entity.setCustomName(Text.literal("§7[" + rank.getFormatting() + "Rank " + rank.getLabel() + "§7] §f" + baseName));
        entity.setCustomNameVisible(true);
    }

    public static void equipArmorForRank(MobEntity entity, MobRank rank) {
        switch (rank) {
            case E -> {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            }
            case D -> {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            }
            case C -> {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            }
            case B -> {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(AscendItems.MYTHRIL_HELMET));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(AscendItems.MYTHRIL_CHESTPLATE));
                entity.equipStack(EquipmentSlot.LEGS, new ItemStack(AscendItems.MYTHRIL_LEGGINGS));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(AscendItems.MYTHRIL_BOOTS));
            }
            case A -> {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(AscendItems.DRAGONITE_HELMET));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(AscendItems.DRAGONITE_CHESTPLATE));
                entity.equipStack(EquipmentSlot.LEGS, new ItemStack(AscendItems.DRAGONITE_LEGGINGS));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(AscendItems.DRAGONITE_BOOTS));
            }
            case S -> {
                ItemStack helm = new ItemStack(AscendItems.VOIDSTEEL_HELMET);
                helm.addEnchantment(Enchantments.PROTECTION, 4);
                ItemStack chest = new ItemStack(AscendItems.VOIDSTEEL_CHESTPLATE);
                chest.addEnchantment(Enchantments.PROTECTION, 4);
                ItemStack legs = new ItemStack(AscendItems.VOIDSTEEL_LEGGINGS);
                chest.addEnchantment(Enchantments.PROTECTION, 4);
                ItemStack boots = new ItemStack(AscendItems.VOIDSTEEL_BOOTS);
                boots.addEnchantment(Enchantments.FEATHER_FALLING, 4);

                entity.equipStack(EquipmentSlot.HEAD, helm);
                entity.equipStack(EquipmentSlot.CHEST, chest);
                entity.equipStack(EquipmentSlot.LEGS, legs);
                entity.equipStack(EquipmentSlot.FEET, boots);
            }
        }
    }

    public static void alertNearbyAllies(MobEntity entity, LivingEntity target, double radius) {
        if (entity.getWorld().isClient || target == null) return;

        List<MobEntity> allies = entity.getWorld().getEntitiesByClass(
                MobEntity.class,
                entity.getBoundingBox().expand(radius),
                mob -> mob != entity && mob.isAlive() && mob.getTarget() == null
        );

        for (MobEntity ally : allies) {
            ally.setTarget(target);
        }
    }

    public static void handleDrops(MobEntity entity, MobRank rank, DamageSource damageSource) {
        if (entity.getWorld().isClient) return;

        int essence = rank != null ? rank.getEssenceDrop() : 10;

        // Reward player directly if killed by player
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
            if (data != null) {
                data.addEssenceWithBonus(essence);
                AscendPackets.syncPlayerData(player);
                player.sendMessage(Text.literal("§6✦ +" + essence + " Essence §7(" + rank.getFormatting() + "Rank " + rank.getLabel() + "§7)"), true);
            }
        }

        // Drops based on rank
        float scrollChance = switch (rank != null ? rank : MobRank.E) {
            case E -> 0.08f;
            case D -> 0.12f;
            case C -> 0.18f;
            case B -> 0.25f;
            case A -> 0.35f;
            case S -> 0.50f;
        };

        if (entity.getRandom().nextFloat() < scrollChance) {
            ItemStack drop = switch (rank != null ? rank : MobRank.E) {
                case E -> new ItemStack(AscendItems.SKILL_SCROLL_E);
                case D -> new ItemStack(AscendItems.SKILL_SCROLL_D);
                case C -> new ItemStack(AscendItems.SKILL_SCROLL_C);
                case B -> new ItemStack(AscendItems.SKILL_TOME_B);
                case A -> new ItemStack(AscendItems.SKILL_TOME_A);
                case S -> new ItemStack(AscendItems.SKILL_TOME_S);
            };
            entity.dropStack(drop);
        }

        // High rank material drops
        if (rank == MobRank.B && entity.getRandom().nextFloat() < 0.20f) {
            entity.dropStack(new ItemStack(AscendItems.MYTHRIL_INGOT));
        } else if (rank == MobRank.A && entity.getRandom().nextFloat() < 0.25f) {
            entity.dropStack(new ItemStack(AscendItems.DRAGONITE_INGOT));
        } else if (rank == MobRank.S && entity.getRandom().nextFloat() < 0.35f) {
            entity.dropStack(new ItemStack(AscendItems.VOIDSTEEL_INGOT));
        }
    }

    public static void spawnTelegraphParticles(ServerWorld world, double x, double y, double z, double radius) {
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;
            world.spawnParticles(ParticleTypes.CRIT, px, y + 0.1, pz, 1, 0, 0.05, 0, 0.02);
            world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, y + 0.1, pz, 1, 0, 0.05, 0, 0.01);
        }
    }
}

