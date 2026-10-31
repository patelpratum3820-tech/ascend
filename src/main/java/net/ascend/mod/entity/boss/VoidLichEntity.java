package net.ascend.mod.entity.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VoidLichEntity extends AscendBossEntity {
    private int castCooldown = 60;

    public VoidLichEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "Void Lich - Demon Lord", BossBar.Color.RED, 3, 3000);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!getWorld().isClient() && getTarget() != null) {
            castCooldown--;
            if (castCooldown <= 0) {
                castCooldown = 70;
                Vec3d targetPos = getTarget().getPos();
                Vec3d dir = targetPos.subtract(getPos()).normalize();

                WitherSkullEntity skull = new WitherSkullEntity(getWorld(), this, dir.x, dir.y, dir.z);
                skull.setPosition(getX(), getY() + 2.0, getZ());
                getWorld().spawnEntity(skull);

                if (getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.WITCH, getX(), getY() + 2.0, getZ(), 20, 0.3, 0.3, 0.3, 0.1);
                    sw.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 1.2f, 1.2f);
                }
            }
        }
    }
}

