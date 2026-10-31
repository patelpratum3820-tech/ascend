package net.ascend.mod.skills;

import net.ascend.mod.data.PlayerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class AscendSkills {

    public static boolean executeSkill(int slotIndex, String skillId, ServerPlayerEntity player, PlayerData data) {
        if (data.getSkillCooldown(slotIndex) > 0) return false;
        ServerWorld world = player.getServerWorld();
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getPos();

        switch (skillId.toLowerCase()) {
            case "shadow_step" -> {
                // Mobility dash with afterimage particles
                if (!data.consumeStamina(25.0)) return false;
                data.setSkillCooldown(slotIndex, 60); // 3 sec cooldown
                data.setInvulnerableTicks(10); // 0.5s i-frame

                player.setVelocity(look.x * 1.8, 0.2, look.z * 1.8);
                player.velocityModified = true;

                world.spawnParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1, pos.z, 30, 0.5, 0.5, 0.5, 0.2);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.2f);
                return true;
            }

            case "flame_slash" -> {
                if (!data.consumeStamina(20.0)) return false;
                data.setSkillCooldown(slotIndex, 80); // 4 sec cooldown

                // Fire wave in front
                for (int i = 1; i <= 5; i++) {
                    Vec3d p = pos.add(look.multiply(i)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 15, 0.4, 0.4, 0.4, 0.05);
                }

                Box hitBox = player.getBoundingBox().offset(look.multiply(2.5)).expand(2.5, 1.5, 2.5);
                List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());
                float damage = 6.0f + (data.getStrength() * 0.5f);
                for (LivingEntity target : targets) {
                    target.damage(player.getDamageSources().playerAttack(player), damage);
                    target.setOnFireFor(5);
                }
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return true;
            }

            case "healing_aura" -> {
                if (!data.consumeMana(30.0)) return false;
                data.setSkillCooldown(slotIndex, 160); // 8 sec cooldown

                player.heal(4.0f + (data.getVitality() * 0.1f));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y + 1, pos.z, 35, 1.0, 1.0, 1.0, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.4f);
                return true;
            }

            case "summon_soldier" -> {
                if (!data.consumeMana(40.0)) return false;
                data.setSkillCooldown(slotIndex, 200); // 10 sec cooldown

                // Spawn Shadow Soldier (Wither Skeleton ally)
                WitherSkeletonEntity minion = EntityType.WITHER_SKELETON.create(world);
                if (minion != null) {
                    minion.refreshPositionAndAngles(pos.x + look.x * 2, pos.y, pos.z + look.z * 2, player.getYaw(), 0);
                    minion.setCustomName(Text.literal("§8§lShadow Soldier"));
                    minion.setCustomNameVisible(true);
                    minion.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
                    minion.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 1));
                    world.spawnEntity(minion);

                    world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, minion.getX(), minion.getY() + 1, minion.getZ(), 25, 0.3, 0.5, 0.3, 0.05);
                    world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.5f, 1.6f);
                }
                return true;
            }

            case "mana_bolt" -> {
                if (!data.consumeMana(15.0)) return false;
                data.setSkillCooldown(slotIndex, 40); // 2 sec cooldown

                SmallFireballEntity bolt = new SmallFireballEntity(world, player, look.x * 2.0, look.y * 2.0, look.z * 2.0);
                bolt.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                world.spawnEntity(bolt);

                world.spawnParticles(ParticleTypes.SOUL, pos.x + look.x, player.getEyeY(), pos.z + look.z, 15, 0.2, 0.2, 0.2, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.4f);
                return true;
            }

            case "arrow_rain" -> {
                if (!data.consumeStamina(30.0)) return false;
                data.setSkillCooldown(slotIndex, 140); // 7 sec cooldown

                HitResult ray = player.raycast(30.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(15));

                for (int i = 0; i < 10; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 6.0;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 6.0;
                    ArrowEntity arrow = new ArrowEntity(world, targetPos.x + offsetX, targetPos.y + 12 + (i * 0.5), targetPos.z + offsetZ);
                    arrow.setVelocity(0, -1.8, 0);
                    arrow.setOwner(player);
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    world.spawnEntity(arrow);
                }
                world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.5f, 0.8f);
                return true;
            }

            case "iron_skin" -> {
                if (!data.consumeStamina(25.0)) return false;
                data.setSkillCooldown(slotIndex, 240); // 12 sec cooldown

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 140, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 140, 0));
                world.spawnParticles(ParticleTypes.CRIT, pos.x, pos.y + 1, pos.z, 30, 0.5, 0.8, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.8f, 1.2f);
                return true;
            }

            case "lightning_strike" -> {
                if (!data.consumeMana(35.0)) return false;
                data.setSkillCooldown(slotIndex, 160); // 8 sec cooldown

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
                if (!data.consumeStamina(30.0)) return false;
                data.setSkillCooldown(slotIndex, 300); // 15 sec cooldown

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 1));
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y + 1.2, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 1.0f, 1.4f);
                return true;
            }

            case "soul_bond" -> {
                if (!data.consumeMana(25.0)) return false;
                data.setSkillCooldown(slotIndex, 240); // 12 sec cooldown

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
                world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y + 1, pos.z, 30, 0.6, 0.8, 0.6, 0.05);
                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return true;
            }
        }
        return false;
    }

    public static boolean executeSignatureMove(ServerPlayerEntity player, PlayerData data) {
        if (data.getSignatureCooldownTicks() > 0) return false;
        ServerWorld world = player.getServerWorld();
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getPos();
        String pClass = data.getPlayerClass().toLowerCase();

        switch (pClass) {
            case "swordsman" -> {
                // Signature: Triple Slash
                if (!data.consumeStamina(40.0)) return false;
                data.setSignatureCooldownTicks(300); // 15 sec

                for (int step = 1; step <= 3; step++) {
                    Vec3d p = pos.add(look.multiply(step * 2)).add(0, 1, 0);
                    world.spawnParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 8, 0.6, 0.6, 0.6, 0.0);
                    world.spawnParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 15, 0.5, 0.5, 0.5, 0.1);
                }

                Box hitBox = player.getBoundingBox().offset(look.multiply(3.0)).expand(3.5, 2.0, 3.5);
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
