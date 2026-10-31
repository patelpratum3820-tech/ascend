package net.ascend.mod.entity.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShadowKnightEntity extends AscendBossEntity {
    private int teleportCooldown = 80;

    public ShadowKnightEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "Shadow Knight", BossBar.Color.PURPLE, 2, 600);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.35, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!getWorld().isClient() && getTarget() != null) {
            teleportCooldown--;
            if (teleportCooldown <= 0 && distanceTo(getTarget()) > 4.0) {
                teleportCooldown = 100;
                Vec3d targetPos = getTarget().getPos();
                Vec3d look = getTarget().getRotationVector();
                double tx = targetPos.x - look.x * 2.0;
                double tz = targetPos.z - look.z * 2.0;
                double ty = targetPos.y;

                if (getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 30, 0.5, 0.8, 0.5, 0.1);
                    refreshPositionAndAngles(tx, ty, tz, getYaw(), getPitch());
                    sw.spawnParticles(ParticleTypes.PORTAL, tx, ty + 1, tz, 30, 0.5, 0.8, 0.5, 0.1);
                    sw.playSound(null, tx, ty, tz, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.2f, 1.0f);
                }
            }
        }
    }
}
