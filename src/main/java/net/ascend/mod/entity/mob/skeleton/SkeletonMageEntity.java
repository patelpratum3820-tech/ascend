package net.ascend.mod.entity.mob.skeleton;

import net.ascend.mod.entity.mob.ai.KiteGoal;
import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkeletonMageEntity extends SkeletonEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(SkeletonMageEntity.class, TrackedDataHandlerRegistry.STRING);
    private int spellCooldown = 0;
    private int teleportCooldown = 0;

    public SkeletonMageEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.24)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new KiteGoal(this, 6.0, 1.2));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
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
        return "Skeleton Mage";
    }

    private void equipWeaponsForRank(MobRank rank) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BLAZE_ROD));
    }

    @Override
    public void tick() {
        super.tick();

        if (spellCooldown > 0) spellCooldown--;
        if (teleportCooldown > 0) teleportCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 14.0);

            double distSq = squaredDistanceTo(target);

            // Teleport retreat if target rushes close (< 3.5 blocks)
            if (distSq < 12.25 && teleportCooldown <= 0) {
                teleportCooldown = 140;
                teleportAway(target);
            }

            // Cast magic fireball / flame bolt
            if (spellCooldown <= 0 && distSq < 324.0) {
                spellCooldown = 50;
                castMagicBolt(target);
            }
        }
    }

    private void castMagicBolt(LivingEntity target) {
        Vec3d dir = target.getPos().add(0, target.getHeight() * 0.5, 0).subtract(this.getX(), this.getEyeY(), this.getZ()).normalize();
        SmallFireballEntity fireball = new SmallFireballEntity(this.getWorld(), this, dir.x, dir.y, dir.z);
        fireball.setPos(this.getX(), this.getEyeY(), this.getZ());
        this.getWorld().spawnEntity(fireball);
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0f, 1.2f);
    }

    private void teleportAway(LivingEntity threat) {
        Vec3d away = this.getPos().subtract(threat.getPos()).normalize();
        double targetX = this.getX() + away.x * 7.0 + (this.random.nextDouble() - 0.5) * 3.0;
        double targetZ = this.getZ() + away.z * 7.0 + (this.random.nextDouble() - 0.5) * 3.0;
        double targetY = this.getY();

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.4, 0.5, 0.4, 0.05);
            this.teleport(targetX, targetY, targetZ);
            serverWorld.spawnParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.0, this.getZ(), 25, 0.4, 0.5, 0.4, 0.1);
            this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
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
