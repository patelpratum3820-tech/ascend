package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ZombieSpearmanEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieSpearmanEntity.class, TrackedDataHandlerRegistry.STRING);
    private int lungeCooldown = 0;

    public ZombieSpearmanEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RANK_DATA, "E");
    }

    @Override
    public MobRank getMobRank() {
        return MobRank.fromString(this.dataTracker.get(RANK_DATA));
    }

    @Override
    public void setMobRank(MobRank rank) {
        this.dataTracker.set(RANK_DATA, rank.name());
        RankedMobHelper.applyRank(this, rank, getMobBaseName());
        RankedMobHelper.equipArmorForRank(this, rank);
        equipWeaponsForRank(rank);
    }

    @Override
    public String getMobBaseName() {
        return "Zombie Spearman";
    }

    private void equipWeaponsForRank(MobRank rank) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
    }

    @Override
    public void tick() {
        super.tick();

        if (lungeCooldown > 0) lungeCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 12.0);

            // Spearman lunging thrust from 4 to 7 blocks distance
            double distSq = squaredDistanceTo(target);
            if (distSq >= 16.0 && distSq <= 49.0 && lungeCooldown <= 0 && this.isOnGround()) {
                lungeCooldown = 80; // 4s cooldown
                Vec3d dir = target.getPos().subtract(this.getPos()).normalize();
                this.setVelocity(dir.x * 1.1, 0.25, dir.z * 1.1);
                this.velocityModified = true;
                this.playSound(SoundEvents.ITEM_TRIDENT_THROW, 1.0f, 1.2f);

                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.2, 0.2, 0.2, 0.05);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        RankedMobHelper.handleDrops(this, getMobRank(), damageSource);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("AscendRank", getMobRank().name());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AscendRank")) {
            setMobRank(MobRank.fromString(nbt.getString("AscendRank")));
        }
    }
}

