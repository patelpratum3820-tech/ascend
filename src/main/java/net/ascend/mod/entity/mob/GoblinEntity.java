package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

public class GoblinEntity extends HostileEntity {
    private boolean isLeader = false;
    private int fleeTicks = 0;

    public GoblinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        // Flee from player when low HP or panicked
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PlayerEntity.class, 12.0f, 1.3, 1.5, entity -> this.fleeTicks > 0 || this.getHealth() < 6.0f));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (fleeTicks > 0) {
            fleeTicks--;
        }

        // Pack AI check every 40 ticks
        if (!this.getWorld().isClient && this.age % 40 == 0) {
            List<GoblinEntity> pack = this.getWorld().getEntitiesByClass(GoblinEntity.class, this.getBoundingBox().expand(12.0), g -> g != this && g.isAlive());
            if (pack.size() >= 2) {
                // Pack boost: courage & swiftness
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.34);
            } else {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.30);
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            // If this goblin was strong or leader, nearby goblins panic!
            List<GoblinEntity> pack = this.getWorld().getEntitiesByClass(GoblinEntity.class, this.getBoundingBox().expand(10.0), g -> g != this && g.isAlive());
            for (GoblinEntity goblin : pack) {
                goblin.panicFlee(100); // 5 seconds panic
            }
        }
    }

    public void panicFlee(int ticks) {
        this.fleeTicks = ticks;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        int iron = 1 + this.random.nextInt(3 + lootingMultiplier);
        this.dropStack(new ItemStack(Items.IRON_NUGGET, iron));
        if (this.random.nextFloat() < 0.35f + (0.1f * lootingMultiplier)) {
            this.dropStack(new ItemStack(AscendItems.SHADOW_ESSENCE, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }
}
