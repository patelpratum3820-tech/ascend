package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class FireDrakeEntity extends PhantomEntity {

    public FireDrakeEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.random.nextInt(3) == 0) {
            this.getWorld().addParticle(
                    ParticleTypes.FLAME,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.8,
                    this.getY() + this.random.nextDouble() * 0.8,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.8,
                    0.0, 0.03, 0.0
            );
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        int scales = 1 + this.random.nextInt(3 + lootingMultiplier);
        this.dropStack(new ItemStack(AscendItems.DRAGON_SCALE, scales));
        this.dropStack(new ItemStack(Items.BLAZE_POWDER, 1 + this.random.nextInt(2)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE;
    }
}
