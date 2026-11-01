package net.ascend.mod.entity.mob.skeleton;

import net.ascend.mod.entity.mob.ai.KiteGoal;
import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
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
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonCrossbowmanEntity extends SkeletonEntity implements AscendRankedMob, RangedAttackMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(SkeletonCrossbowmanEntity.class, TrackedDataHandlerRegistry.STRING);

    public SkeletonCrossbowmanEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.24)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new KiteGoal(this, 6.0, 1.2));
        this.goalSelector.add(2, new BowAttackGoal<>(this, 1.0, 30, 16.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

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
        return "Skeleton Crossbowman";
    }

    private void equipWeaponsForRank(MobRank rank) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        PersistentProjectileEntity projectile = ProjectileUtil.createArrowProjectile(this, arrowStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        projectile.setVelocity(d, e + g * 0.2, f, 2.2f, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        projectile.setPierceLevel((byte) 2);
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.getWorld().spawnEntity(projectile);
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
