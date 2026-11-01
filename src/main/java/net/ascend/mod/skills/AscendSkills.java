package net.ascend.mod.skills;

import net.ascend.mod.data.PlayerData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AscendSkills {

    public static boolean executeSkill(int slotIndex, String skillId, ServerPlayerEntity player, PlayerData data) {
        if (data.getSkillCooldown(slotIndex) > 0) return false;
        if (data.isResourceLocked()) {
            player.sendMessage(Text.literal("§cYour energy is locked (" + (data.getResourceLockedTicks() / 20) + "s remaining)!"), true);
            return false;
        }

        ServerWorld world = player.getServerWorld();
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getPos();

        SkillInfo info = SkillInfo.getById(skillId);
        int cooldownTicks = Math.max(20, info.cooldownSec() * 20);

        // Deduct cost based on info
        double cost = info.costAmount();
        if (info.isPercent()) {
            if ("MANA".equals(info.costType())) {
                cost = (data.getMana() * cost) / 100.0;
            } else if ("STAMINA".equals(info.costType())) {
                cost = (100.0 * cost) / 100.0;
            }
        }

        if ("MANA".equals(info.costType())) {
            if (!data.consumeMana(cost)) return false;
        } else if ("STAMINA".equals(info.costType())) {
            if (!data.consumeStamina(cost)) return false;
        }

        // Set cooldown
        data.setSkillCooldown(slotIndex, cooldownTicks);

        switch (skillId.toLowerCase()) {
            // ==================== E RANK SKILLS ====================
            case "shadow_step", "basic_step", "dash" -> {
                data.setInvulnerableTicks(10);
                player.setVelocity(look.x * 1.8, 0.2, look.z * 1.8);
                player.velocityModified = true;
                world.spawnParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1, pos.z, 30, 0.5, 0.5, 0.5, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.2f);
                return true;
            }

            case "flash_step" -> {
                data.setInvulnerableTicks(15);
                Vec3d targetPos = pos.add(look.multiply(6.0));
                HitResult hit = player.raycast(6.0, 0, false);
                if (hit.getType() != HitResult.Type.MISS) {
                    targetPos = hit.getPos().subtract(look.multiply(0.5));
                }
                player.teleport(world, targetPos.x, targetPos.y, targetPos.z, player.getYaw(), player.getPitch());
                world.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, targetPos.x, targetPos.y + 1, targetPos.z, 30, 0.5, 0.8, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.8f, 1.6f);
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.2f, 1.5f);
                return true;
            }

            case "flame_slash" -> {
                for (int i = 1; i <= 5; i++) {
                    Vec3d p = pos.add(look.multiply(i)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 15, 0.4, 0.4, 0.4, 0.05);
                }
                Box hitBox = player.getBoundingBox().offset(look.multiply(2.5)).expand(2.5, 1.5, 2.5);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float damage = 8.0f + (data.getStrength() * 0.6f);
                for (LivingEntity target : targets) {
                    target.damage(player.getDamageSources().playerAttack(player), damage);
                    target.setOnFireFor(5);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return true;
            }

            case "mana_bolt" -> {
                SmallFireballEntity bolt = new SmallFireballEntity(world, player, look.x * 2.0, look.y * 2.0, look.z * 2.0);
                bolt.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                world.spawnEntity(bolt);
                world.spawnParticles(ParticleTypes.SOUL, pos.x + look.x, player.getEyeY(), pos.z + look.z, 15, 0.2, 0.2, 0.2, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.4f);
                return true;
            }

            case "block" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 3));
                world.spawnParticles(ParticleTypes.CRIT, pos.x + look.x, pos.y + 1, pos.z + look.z, 15, 0.3, 0.3, 0.3, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.5f, 1.0f);
                return true;
            }

            case "power_strike" -> {
                Box hitBox = player.getBoundingBox().offset(look.multiply(2.0)).expand(2.0, 1.5, 2.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float damage = 14.0f + (data.getStrength() * 1.5f);
                for (LivingEntity t : targets) {
                    t.damage(player.getDamageSources().playerAttack(player), damage);
                    t.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30, 4));
                    world.spawnParticles(ParticleTypes.CRIT, t.getX(), t.getY() + 1, t.getZ(), 25, 0.4, 0.4, 0.4, 0.2);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "enchant" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1200, 0));
                world.spawnParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 1, pos.z, 40, 0.6, 0.8, 0.6, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.2f, 1.2f);
                player.sendMessage(Text.literal("§b✦ Weapons & tools enchanted with +Strength & +Haste for 60s!"), true);
                return true;
            }

            case "reinforce" -> {
                ItemStack main = player.getMainHandStack();
                if (main != null && main.isDamageable()) {
                    main.setDamage(Math.max(0, main.getDamage() - 250));
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1));
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y + 1, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1.0f, 1.4f);
                player.sendMessage(Text.literal("§a✦ Gear reinforced and durability restored!"), true);
                return true;
            }

            // ==================== D RANK SKILLS ====================
            case "whirlwind" -> {
                world.spawnParticles(ParticleTypes.SWEEP_ATTACK, pos.x, pos.y + 1, pos.z, 20, 1.5, 0.2, 1.5, 0.0);
                Box box = player.getBoundingBox().expand(4.0, 1.5, 4.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive());
                float dmg = 10.0f + (data.getStrength() * 0.7f);
                for (LivingEntity t : targets) {
                    t.damage(player.getDamageSources().playerAttack(player), dmg);
                    t.takeKnockback(1.0, player.getX() - t.getX(), player.getZ() - t.getZ());
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.5f, 0.9f);
                return true;
            }

            case "frost_nova" -> {
                world.spawnParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 0.5, pos.z, 50, 3.0, 0.3, 3.0, 0.1);
                Box box = player.getBoundingBox().expand(5.0, 2.0, 5.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive());
                for (LivingEntity t : targets) {
                    t.damage(player.getDamageSources().magic(), 8.0f);
                    t.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 140, 2));
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.5f, 1.2f);
                return true;
            }

            case "arrow_rain" -> {
                HitResult ray = player.raycast(30.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(15));
                for (int i = 0; i < 12; i++) {
                    double ox = (world.random.nextDouble() - 0.5) * 6.0;
                    double oz = (world.random.nextDouble() - 0.5) * 6.0;
                    ArrowEntity arrow = new ArrowEntity(world, targetPos.x + ox, targetPos.y + 12 + (i * 0.4), targetPos.z + oz);
                    arrow.setVelocity(0, -1.8, 0);
                    arrow.setOwner(player);
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    world.spawnEntity(arrow);
                }
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "stone_skin" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 1));
                world.spawnParticles(ParticleTypes.ASH, pos.x, pos.y + 1, pos.z, 40, 0.5, 0.8, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.PLAYERS, 1.5f, 0.7f);
                return true;
            }

            case "spirit_wolf" -> {
                WolfEntity wolf = EntityType.WOLF.create(world);
                if (wolf != null) {
                    wolf.refreshPositionAndAngles(pos.x + look.x * 2, pos.y, pos.z + look.z * 2, player.getYaw(), 0);
                    wolf.setOwner(player);
                    wolf.setTamed(true);
                    wolf.setCustomName(Text.literal("§b§lSpirit Wolf"));
                    wolf.setCustomNameVisible(true);
                    wolf.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
                    wolf.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1));
                    world.spawnEntity(wolf);
                    world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 20, 0.4, 0.4, 0.4, 0.05);
                    world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WOLF_HOWL, SoundCategory.PLAYERS, 1.0f, 1.2f);
                }
                return true;
            }

            case "grapple_shot" -> {
                HitResult ray = player.raycast(20.0, 0, false);
                Vec3d target = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(12.0));
                Vec3d pull = target.subtract(pos).normalize().multiply(1.8);
                player.setVelocity(pull.x, Math.max(0.4, pull.y + 0.2), pull.z);
                player.velocityModified = true;
                data.setInvulnerableTicks(15);
                world.spawnParticles(ParticleTypes.FISHING, pos.x, pos.y + 1, pos.z, 20, 0.3, 0.3, 0.3, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.PLAYERS, 1.5f, 1.2f);
                return true;
            }

            // ==================== C RANK SKILLS ====================
            case "chain_fire" -> {
                Box box = player.getBoundingBox().offset(look.multiply(5)).expand(8.0, 4.0, 8.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive());
                int struck = 0;
                for (LivingEntity t : targets) {
                    if (struck++ >= 5) break;
                    t.damage(player.getDamageSources().magic(), 12.0f);
                    t.setOnFireFor(6);
                    world.spawnParticles(ParticleTypes.LAVA, t.getX(), t.getY() + 1, t.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.2f, 1.2f);
                return true;
            }

            case "lightning_strike" -> {
                HitResult ray = player.raycast(25.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(10));
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(targetPos);
                    world.spawnEntity(lightning);
                }
                return true;
            }

            case "berserk" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1));
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y + 1.2, pos.z, 25, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 1.0f, 1.4f);
                return true;
            }

            case "reflect" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
                Box box = player.getBoundingBox().expand(3.0);
                for (LivingEntity t : world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
                    t.damage(player.getDamageSources().thorns(player), 10.0f);
                }
                world.spawnParticles(ParticleTypes.ENCHANTED_HIT, pos.x, pos.y + 1, pos.z, 30, 0.5, 0.5, 0.5, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.5f, 1.5f);
                return true;
            }

            case "explosive_arrow" -> {
                SmallFireballEntity fb = new SmallFireballEntity(world, player, look.x * 2.5, look.y * 2.5, look.z * 2.5);
                fb.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                world.spawnEntity(fb);
                world.spawnParticles(ParticleTypes.FLAME, pos.x + look.x, player.getEyeY(), pos.z + look.z, 20, 0.2, 0.2, 0.2, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.2f, 1.4f);
                return true;
            }

            case "blood_rush" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 0));
                world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, pos.x, pos.y + 1, pos.z, 25, 0.4, 0.4, 0.4, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "summon_shadow_soldier" -> {
                world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 0.2, pos.z, 40, 2.0, 0.2, 2.0, 0.2);
                for (int i = 0; i < 2; i++) {
                    WitherSkeletonEntity m = EntityType.WITHER_SKELETON.create(world);
                    if (m != null) {
                        m.refreshPositionAndAngles(pos.x + (i == 0 ? -1.5 : 1.5), pos.y, pos.z + 1.0, player.getYaw(), 0);
                        m.setCustomName(Text.literal("§5§lShadow Soldier"));
                        m.setCustomNameVisible(true);
                        m.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
                        world.spawnEntity(m);
                    }
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.4f);
                return true;
            }

            case "haste_aura" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1));
                Box box = player.getBoundingBox().expand(10.0);
                for (ServerPlayerEntity ally : world.getEntitiesByClass(ServerPlayerEntity.class, box, p -> p != player)) {
                    ally.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
                    ally.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1));
                }
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y + 1, pos.z, 40, 1.0, 0.5, 1.0, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.5f, 1.5f);
                return true;
            }

            case "rune_carve" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0));
                world.spawnParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 1, pos.z, 50, 0.8, 1.0, 0.8, 0.3);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.5f, 0.9f);
                player.sendMessage(Text.literal("§5✦ Ancient runes carved into equipment! +Absorption & +Resistance!"), true);
                return true;
            }

            // ==================== B RANK SKILLS ====================
            case "mana_shield" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 3));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200, 2));
                world.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1, pos.z, 40, 0.8, 1.0, 0.8, 0.05);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.2f, 1.5f);
                return true;
            }

            case "triple_slash" -> {
                for (int i = 1; i <= 3; i++) {
                    Vec3d p = pos.add(look.multiply(i * 1.5)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 4, 0.5, 0.5, 0.5, 0.05);
                }
                Box hitBox = player.getBoundingBox().offset(look.multiply(3)).expand(3.0, 2.0, 3.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float dmg = 14.0f + (data.getStrength() * 1.0f);
                for (LivingEntity t : targets) {
                    t.damage(player.getDamageSources().playerAttack(player), dmg);
                    t.setVelocity(look.x * 1.2, 0.3, look.z * 1.2);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.5f, 0.9f);
                return true;
            }

            case "piercing_volley" -> {
                for (int i = -2; i <= 2; i++) {
                    ArrowEntity arrow = new ArrowEntity(world, player);
                    arrow.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                    Vec3d spreadLook = look.rotateY((float) Math.toRadians(i * 6));
                    arrow.setVelocity(spreadLook.x, spreadLook.y, spreadLook.z, 3.2f, 1.0f);
                    arrow.setDamage(7.0 + (data.getStrength() * 0.5));
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    world.spawnEntity(arrow);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.8f, 0.6f);
                return true;
            }

            case "guardian_slam" -> {
                world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y + 0.2, pos.z, 35, 3.0, 0.2, 3.0, 0.1);
                Box box = player.getBoundingBox().expand(5.0, 2.5, 5.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive());
                float slamDmg = 10.0f + (data.getVitality() * 0.4f);
                for (LivingEntity t : targets) {
                    t.damage(player.getDamageSources().playerAttack(player), slamDmg);
                    t.setVelocity(0, 0.8, 0);
                    t.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 2));
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "summon_construct" -> {
                IronGolemEntity golem = EntityType.IRON_GOLEM.create(world);
                if (golem != null) {
                    golem.refreshPositionAndAngles(pos.x + look.x * 2, pos.y, pos.z + look.z * 2, player.getYaw(), 0);
                    golem.setCustomName(Text.literal("§6§lForged Construct"));
                    golem.setCustomNameVisible(true);
                    golem.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1600, 1));
                    golem.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1600, 1));
                    world.spawnEntity(golem);
                    world.spawnParticles(ParticleTypes.FLAME, golem.getX(), golem.getY() + 1.5, golem.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
                    world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.2f, 0.8f);
                }
                return true;
            }

            case "phoenix_familiar" -> {
                BlazeEntity phoenix = EntityType.BLAZE.create(world);
                if (phoenix != null) {
                    phoenix.refreshPositionAndAngles(pos.x + 2, pos.y + 1, pos.z, player.getYaw(), 0);
                    phoenix.setCustomName(Text.literal("§6§lPhoenix Familiar"));
                    phoenix.setCustomNameVisible(true);
                    world.spawnEntity(phoenix);
                }
                player.heal(12.0f);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200, 0));
                world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y + 1, pos.z, 50, 1.0, 1.0, 1.0, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.5f, 1.2f);
                return true;
            }

            case "forge_legend" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 900, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 900, 1));
                world.spawnParticles(ParticleTypes.LAVA, pos.x, pos.y + 1, pos.z, 30, 0.6, 0.6, 0.6, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1.5f, 0.6f);
                player.sendMessage(Text.literal("§6✦ Divine Forge Aura ignited! Strength III & Resistance II active!"), true);
                return true;
            }

            // ==================== A RANK SKILLS ====================
            case "undead_army" -> {
                world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + 0.2, pos.z, 80, 4.0, 0.5, 4.0, 0.2);
                for (int i = 0; i < 10; i++) {
                    ZombieEntity z = EntityType.ZOMBIE.create(world);
                    if (z != null) {
                        double angle = (2 * Math.PI / 10) * i;
                        double spawnX = pos.x + Math.cos(angle) * 3.5;
                        double spawnZ = pos.z + Math.sin(angle) * 3.5;
                        z.refreshPositionAndAngles(spawnX, pos.y, spawnZ, (float) Math.toDegrees(angle), 0);
                        z.setCustomName(Text.literal("§2§lUndead Soldier"));
                        z.setCustomNameVisible(true);
                        z.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                        z.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                        z.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
                        z.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1));
                        world.spawnEntity(z);
                    }
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.5f, 0.7f);
                player.sendMessage(Text.literal("§2§l[UNDEAD ARMY] §f10 Undead Soldiers arise to fight by your side!"), true);
                return true;
            }

            case "meteor_call" -> {
                HitResult ray = player.raycast(45.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(20));
                FireballEntity meteor = new FireballEntity(world, player, 0, -2.5, 0, 4);
                meteor.setPosition(targetPos.x, targetPos.y + 25, targetPos.z);
                world.spawnEntity(meteor);
                world.spawnParticles(ParticleTypes.LAVA, targetPos.x, targetPos.y + 1, targetPos.z, 60, 2.5, 1.0, 2.5, 0.2);
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 0.6f);
                return true;
            }

            case "soul_bond" -> {
                player.heal(player.getMaxHealth() * 0.5f);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1));
                world.spawnParticles(ParticleTypes.HEART, pos.x, pos.y + 1, pos.z, 25, 0.8, 0.8, 0.8, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.5f);
                return true;
            }

            case "revive_token" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 2));
                world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y + 1, pos.z, 60, 1.0, 1.5, 1.0, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.5f, 1.0f);
                player.sendMessage(Text.literal("§e✦ Sacred Revive Token active! High Absorption and Regeneration granted!"), true);
                return true;
            }

            case "blade_tempest" -> {
                for (int i = 0; i < 16; i++) {
                    double angle = (2 * Math.PI / 16) * i;
                    double px = pos.x + Math.cos(angle) * 3.0;
                    double pz = pos.z + Math.sin(angle) * 3.0;
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, px, pos.y + 1, pz, 2, 0.2, 0.2, 0.2, 0.0);
                }
                Box stormBox = player.getBoundingBox().expand(5.0, 2.5, 5.0);
                for (LivingEntity t : world.getEntitiesByClass(LivingEntity.class, stormBox, e -> e != player && e.isAlive())) {
                    t.damage(player.getDamageSources().playerAttack(player), 22.0f + (data.getStrength() * 1.2f));
                    t.takeKnockback(1.5, player.getX() - t.getX(), player.getZ() - t.getZ());
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 2.0f, 0.6f);
                return true;
            }

            // ==================== S RANK SKILLS ====================
            case "meteor_storm" -> {
                HitResult ray = player.raycast(50.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(25));
                for (int i = 0; i < 6; i++) {
                    double ox = (world.random.nextDouble() - 0.5) * 14.0;
                    double oz = (world.random.nextDouble() - 0.5) * 14.0;
                    FireballEntity meteor = new FireballEntity(world, player, 0, -2.5, 0, 3);
                    meteor.setPosition(targetPos.x + ox, targetPos.y + 30 + (i * 3), targetPos.z + oz);
                    world.spawnEntity(meteor);
                }
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 2.0f, 0.5f);
                player.sendMessage(Text.literal("§c§l[METEOR STORM] §fCataclysmic meteors rain upon the battlefield!"), true);
                return true;
            }

            case "legion_of_shadows" -> {
                world.spawnParticles(ParticleTypes.SQUID_INK, pos.x, pos.y + 0.2, pos.z, 100, 6.0, 0.5, 6.0, 0.2);
                for (int i = 0; i < 15; i++) {
                    double angle = (2 * Math.PI / 15) * i;
                    double sx = pos.x + Math.cos(angle) * 5.0;
                    double sz = pos.z + Math.sin(angle) * 5.0;
                    WitherSkeletonEntity soldier = EntityType.WITHER_SKELETON.create(world);
                    if (soldier != null) {
                        soldier.refreshPositionAndAngles(sx, pos.y, sz, (float) Math.toDegrees(angle), 0);
                        soldier.setCustomName(Text.literal("§5§lShadow Legion"));
                        soldier.setCustomNameVisible(true);
                        soldier.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1800, 1));
                        soldier.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1800, 1));
                        world.spawnEntity(soldier);
                    }
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 2.0f, 0.7f);
                player.sendMessage(Text.literal("§5§l[LEGION OF SHADOWS] §f15 Shadow Legionnaires rise to conquer!"), true);
                return true;
            }

            case "absolute_guard" -> {
                data.setInvulnerableTicks(100);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 5));
                world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y + 1, pos.z, 60, 1.0, 1.5, 1.0, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.5f, 1.0f);
                return true;
            }

            case "phantom_barrage" -> {
                Box area = player.getBoundingBox().expand(15.0, 6.0, 15.0);
                List<LivingEntity> enemies = world.getEntitiesByClass(LivingEntity.class, area, e -> e != player && e.isAlive());
                float dmg = 25.0f + (data.getStrength() * 1.5f);
                for (LivingEntity e : enemies) {
                    e.damage(player.getDamageSources().playerAttack(player), dmg);
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, e.getX(), e.getY() + 1, e.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
                    world.spawnParticles(ParticleTypes.PORTAL, e.getX(), e.getY() + 1, e.getZ(), 15, 0.3, 0.5, 0.3, 0.1);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 2.0f, 0.5f);
                return true;
            }

            case "dragon_awakening" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 3));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 40, 1));
                world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y + 1, pos.z, 100, 2.0, 2.0, 2.0, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 2.0f, 0.7f);
                player.sendMessage(Text.literal("§6§l[DRAGON AWAKENING] §cThe ancient dragon blood surges within you!"), true);
                return true;
            }

            // ==================== SS RANK SKILLS ====================
            case "judgment_meteor_shower" -> {
                data.setResourceLockedTicks(1200);
                data.setSkillCooldown(slotIndex, 36000);

                Box area = player.getBoundingBox().expand(10.0, 6.0, 10.0);
                List<MobEntity> hostiles = world.getEntitiesByClass(MobEntity.class, area, LivingEntity::isAlive);

                for (MobEntity mob : hostiles) {
                    damageWithBossCap(mob, player, 9999.0f, 0.05f);
                    world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, mob.getX(), mob.getY() + 1, mob.getZ(), 1, 0, 0, 0, 0);
                    world.spawnParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY() + 2, mob.getZ(), 30, 0.5, 1.0, 0.5, 0.1);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 2.0f, 0.5f);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 0.4f);
                player.sendMessage(Text.literal("§e§l[SS JUDGMENT METEOR SHOWER] §fDivine retribution descends! Mana locked for 60s."), false);
                return true;
            }

            case "sovereigns_domain" -> {
                data.setResourceLockedTicks(1200);
                data.setSkillCooldown(slotIndex, 36000);

                world.spawnParticles(ParticleTypes.SQUID_INK, pos.x, pos.y + 0.1, pos.z, 200, 12.0, 0.5, 12.0, 0.1);
                world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 1, pos.z, 100, 10.0, 1.5, 10.0, 0.1);

                Box domain = player.getBoundingBox().expand(16.0, 8.0, 16.0);
                List<LivingEntity> enemies = world.getEntitiesByClass(LivingEntity.class, domain, e -> e != player && e.isAlive());
                for (LivingEntity e : enemies) {
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 4));
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 2));
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 2.0f, 0.5f);
                player.sendMessage(Text.literal("§e§l[SS SOVEREIGN'S DOMAIN] §5Shadow Monarch authority declared! Enemies crippled. Energy locked 60s."), false);
                return true;
            }

            case "heavens_edge" -> {
                data.setResourceLockedTicks(1200);
                data.setSkillCooldown(slotIndex, 36000);

                for (int i = 1; i <= 20; i++) {
                    Vec3d p = pos.add(look.multiply(i)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 5, 0.8, 0.8, 0.8, 0.0);
                    world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 10, 0.5, 0.5, 0.5, 0.1);
                }

                Box slashBox = player.getBoundingBox().offset(look.multiply(10.0)).expand(12.0, 4.0, 12.0);
                List<MobEntity> targets = world.getEntitiesByClass(MobEntity.class, slashBox, LivingEntity::isAlive);
                for (MobEntity t : targets) {
                    damageWithBossCap(t, player, 9999.0f, 0.10f);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 2.0f, 0.5f);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.5f, 1.2f);
                player.sendMessage(Text.literal("§e§l[SS HEAVEN'S EDGE] §6Dimensional strike cleaved the battlefield! Stamina locked 60s."), false);
                return true;
            }
        }
        return false;
    }

    private static void damageWithBossCap(LivingEntity target, ServerPlayerEntity player, float normalDmg, float bossCapPercent) {
        if (target instanceof net.ascend.mod.entity.boss.AscendBossEntity || target instanceof net.minecraft.entity.boss.WitherEntity || target instanceof net.minecraft.entity.boss.dragon.EnderDragonEntity) {
            float cap = target.getMaxHealth() * bossCapPercent;
            target.damage(player.getDamageSources().playerAttack(player), Math.min(normalDmg, cap));
        } else {
            target.damage(player.getDamageSources().playerAttack(player), normalDmg);
        }
    }

    public static boolean executeSignatureMove(ServerPlayerEntity player, PlayerData data) {
        if (data.getSignatureCooldownTicks() > 0) return false;
        if (data.isResourceLocked()) {
            player.sendMessage(Text.literal("§cYour energy is locked (" + (data.getResourceLockedTicks() / 20) + "s remaining)!"), true);
            return false;
        }

        ServerWorld world = player.getServerWorld();
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getPos();

        switch (data.getPlayerClass().toLowerCase()) {
            case "swordsman" -> {
                // Signature: Triple Slash Blast
                if (!data.consumeStamina(40.0)) return false;
                data.setSignatureCooldownTicks(240); // 12 sec

                for (int i = 1; i <= 3; i++) {
                    Vec3d p = pos.add(look.multiply(i * 1.5)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 5, 0.5, 0.5, 0.5, 0.1);
                    world.spawnParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 20, 0.5, 0.5, 0.5, 0.2);
                }

                Box hitBox = player.getBoundingBox().offset(look.multiply(3)).expand(3.0, 2.0, 3.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float damage = 16.0f + (data.getStrength() * 1.2f);
                for (LivingEntity target : targets) {
                    target.damage(player.getDamageSources().playerAttack(player), damage);
                    target.setVelocity(look.x * 1.5, 0.4, look.z * 1.5);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "summoner" -> {
                // Signature: Summoning Portal
                if (!data.consumeMana(60.0)) return false;
                data.setSignatureCooldownTicks(400); // 20 sec

                world.spawnParticles(ParticleTypes.PORTAL, pos.x, pos.y + 0.2, pos.z, 60, 2.0, 0.2, 2.0, 0.5);
                for (int i = 0; i < 3; i++) {
                    WitherSkeletonEntity m = EntityType.WITHER_SKELETON.create(world);
                    if (m != null) {
                        double ox = (i - 1) * 1.5;
                        m.refreshPositionAndAngles(pos.x + ox, pos.y, pos.z + 1.5, player.getYaw(), 0);
                        m.setCustomName(Text.literal("§5§lShadow Minion"));
                        m.setCustomNameVisible(true);
                        m.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1600, 1));
                        m.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1600, 1));
                        world.spawnEntity(m);
                    }
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.2f);
                return true;
            }

            case "mage" -> {
                // Signature: Meteor Call
                if (!data.consumeMana(70.0)) return false;
                data.setSignatureCooldownTicks(500); // 25 sec

                HitResult ray = player.raycast(40.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(20));

                FireballEntity meteor = new FireballEntity(world, player, 0, -2.5, 0, 3);
                meteor.setPosition(targetPos.x, targetPos.y + 20, targetPos.z);
                world.spawnEntity(meteor);

                world.spawnParticles(ParticleTypes.LAVA, targetPos.x, targetPos.y + 1, targetPos.z, 50, 2.0, 1.0, 2.0, 0.2);
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 0.6f);
                return true;
            }

            case "archer" -> {
                // Signature: Piercing Volley
                if (!data.consumeStamina(45.0)) return false;
                data.setSignatureCooldownTicks(360); // 18 sec

                for (int i = -2; i <= 2; i++) {
                    ArrowEntity arrow = new ArrowEntity(world, player);
                    arrow.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                    Vec3d spreadLook = look.rotateY((float) Math.toRadians(i * 6));
                    arrow.setVelocity(spreadLook.x, spreadLook.y, spreadLook.z, 3.5f, 1.0f);
                    arrow.setDamage(8.0 + (data.getStrength() * 0.6));
                    arrow.setPunch(2);
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    world.spawnEntity(arrow);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 2.0f, 0.5f);
                return true;
            }

            case "tank" -> {
                // Signature: Guardian Slam
                if (!data.consumeStamina(50.0)) return false;
                data.setSignatureCooldownTicks(300); // 15 sec

                world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y + 0.2, pos.z, 40, 3.0, 0.2, 3.0, 0.2);

                Box hitBox = player.getBoundingBox().expand(6.0, 3.0, 6.0);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float slamDmg = 12.0f + (data.getVitality() * 0.4f);
                for (LivingEntity target : targets) {
                    target.damage(player.getDamageSources().playerAttack(player), slamDmg);
                    target.setVelocity(0, 0.9, 0); // launch into air
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.5f, 0.7f);
                return true;
            }
        }
        return false;
    }
}
