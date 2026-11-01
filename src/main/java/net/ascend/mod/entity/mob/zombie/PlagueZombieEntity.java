package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

public class PlagueZombieEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(PlagueZombieEntity.class, TrackedDataHandlerRegistry.STRING);

    public PlagueZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
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
        return "Plague Zombie";
    }

    private void equipWeaponsForRank(MobRank rank) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld() instanceof ServerWorld serverWorld && this.age % 10 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SNEEZE, this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.3, 0.4, 0.3, 0.01);
        }
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit && target instanceof LivingEntity livingTarget) {
            int duration = switch (getMobRank()) {
                case S, A -> 140;
                case B, C -> 100;
                default -> 60;
            };
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, duration, 1));
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 80, 0));
        }
        return hit;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            // Spawn lingering poison gas cloud
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
            cloud.setRadius(3.0f);
            cloud.setRadiusOnUse(-0.5f);
            cloud.setWaitTime(10);
            cloud.setDuration(160);
            cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());
            cloud.addEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
            this.getWorld().spawnEntity(cloud);
            this.playSound(SoundEvents.BLOCK_BREWING_STAND_BREW, 1.2f, 0.5f);
        }
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

