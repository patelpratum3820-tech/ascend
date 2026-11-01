package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.AscendEntities;
import net.ascend.mod.entity.mob.ai.KiteGoal;
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
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ZombieNecromancerEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieNecromancerEntity.class, TrackedDataHandlerRegistry.STRING);
    private int summonCooldown = 60;
    private int castCooldown = 40;

    public ZombieNecromancerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 36.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.24)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        // Kite when player gets too close
        this.goalSelector.add(1, new KiteGoal(this, 5.0, 1.2));
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
        return "Zombie Necromancer";
    }

    private void equipWeaponsForRank(MobRank rank) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE));
    }

    @Override
    public void tick() {
        super.tick();

        if (summonCooldown > 0) summonCooldown--;
        if (castCooldown > 0) castCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 16.0);

            // Dark projectile attack
            if (castCooldown <= 0 && squaredDistanceTo(target) < 400.0) {
                castCooldown = 60; // 3s
                castDarkSkull(target);
            }

            // Summon minions
            if (summonCooldown <= 0) {
                summonCooldown = 280; // 14s
                summonMinions();
            }
        }
    }

    private void castDarkSkull(LivingEntity target) {
        Vec3d dir = target.getPos().add(0, target.getHeight() * 0.5, 0).subtract(this.getX(), this.getEyeY(), this.getZ()).normalize();
        WitherSkullEntity skull = new WitherSkullEntity(this.getWorld(), this, dir.x, dir.y, dir.z);
        skull.setPos(this.getX(), this.getEyeY(), this.getZ());
        this.getWorld().spawnEntity(skull);
        this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 0.8f, 1.2f);
    }

    private void summonMinions() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        this.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);
        int count = getMobRank().ordinal() >= MobRank.B.ordinal() ? 3 : 2;

        for (int i = 0; i < count; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 4.0;
            double oz = (this.random.nextDouble() - 0.5) * 4.0;

            ZombieSoldierEntity minion = new ZombieSoldierEntity(AscendEntities.ZOMBIE_SOLDIER, this.getWorld());
            minion.setPosition(this.getX() + ox, this.getY(), this.getZ() + oz);
            minion.setMobRank(MobRank.E); // Minions are rank E
            minion.setTarget(this.getTarget());
            serverWorld.spawnEntity(minion);

            serverWorld.spawnParticles(ParticleTypes.SOUL, minion.getX(), minion.getY() + 0.5, minion.getZ(), 10, 0.3, 0.5, 0.3, 0.05);
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

