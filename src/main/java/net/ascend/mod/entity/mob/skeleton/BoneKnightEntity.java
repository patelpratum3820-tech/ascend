package net.ascend.mod.entity.mob.skeleton;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoneKnightEntity extends SkeletonEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(BoneKnightEntity.class, TrackedDataHandlerRegistry.STRING);
    private int shieldChargeCooldown = 0;
    private int blockCooldown = 0;

    public BoneKnightEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.15, false));
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
        return "Bone Knight";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case E -> new ItemStack(Items.IRON_SWORD);
            case D, C -> new ItemStack(AscendItems.MYTHRIL_SWORD);
            case B, A -> new ItemStack(AscendItems.DRAGONITE_SWORD);
            case S -> new ItemStack(AscendItems.VOIDSTEEL_SWORD);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
        this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
    }

    @Override
    public void tick() {
        super.tick();

        if (shieldChargeCooldown > 0) shieldChargeCooldown--;
        if (blockCooldown > 0) blockCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 12.0);

            double distSq = squaredDistanceTo(target);
            if (distSq >= 25.0 && distSq <= 100.0 && shieldChargeCooldown <= 0 && this.isOnGround()) {
                shieldChargeCooldown = 120;
                executeShieldCharge(target);
            }
        }
    }

    private void executeShieldCharge(LivingEntity target) {
        Vec3d dir = target.getPos().subtract(this.getPos()).normalize();
        this.setVelocity(dir.x * 1.4, 0.2, dir.z * 1.4);
        this.velocityModified = true;
        this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.2f, 0.7f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }

        float chargeDmg = (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.2);
        target.damage(this.getDamageSources().mobAttack(this), chargeDmg);
        target.takeKnockback(1.8, this.getX() - target.getX(), this.getZ() - target.getZ());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!source.isIndirect() && blockCooldown <= 0 && source.getAttacker() instanceof LivingEntity attacker) {
            double dot = this.getRotationVector().dotProduct(attacker.getPos().subtract(this.getPos()).normalize());
            if (dot > 0.0) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                this.swingHand(Hand.OFF_HAND);
                blockCooldown = 30;
                return super.damage(source, amount * 0.2f);
            }
        }
        return super.damage(source, amount);
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
