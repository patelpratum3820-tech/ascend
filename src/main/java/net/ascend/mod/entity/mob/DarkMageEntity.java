package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DarkMageEntity extends HostileEntity implements RangedAttackMob {

    public DarkMageEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0, 40, 16.0f));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 5.0f, 1.2, 1.3));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        Vec3d dir = target.getPos().subtract(this.getPos()).normalize();
        WitherSkullEntity skull = new WitherSkullEntity(this.getWorld(), this, dir.x, dir.y, dir.z);
        skull.setCharged(false);
        skull.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
        this.getWorld().spawnEntity(skull);
        this.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, 0.8f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.random.nextInt(3) == 0) {
            this.getWorld().addParticle(
                    ParticleTypes.WITCH,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                    this.getY() + this.random.nextDouble() * 1.8,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.05, 0.0
            );
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        int crystals = 2 + this.random.nextInt(4 + lootingMultiplier);
        this.dropStack(new ItemStack(AscendItems.MANA_CRYSTAL, crystals));
        if (this.random.nextFloat() < 0.15f + (0.05f * lootingMultiplier)) {
            this.dropStack(new ItemStack(AscendItems.APPRENTICE_STAFF, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }
}
