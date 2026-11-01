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

public class SkeletonSwordsmanEntity extends SkeletonEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(SkeletonSwordsmanEntity.class, TrackedDataHandlerRegistry.STRING);
    private int dashSlashCooldown = 0;

    public SkeletonSwordsmanEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.25, false));
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
        return "Skeleton Swordsman";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case E -> new ItemStack(Items.STONE_SWORD);
            case D -> new ItemStack(Items.IRON_SWORD);
            case C, B -> new ItemStack(AscendItems.MYTHRIL_KATANA);
            case A -> new ItemStack(AscendItems.DRAGONITE_SWORD);
            case S -> new ItemStack(AscendItems.VOIDSTEEL_SWORD);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
    }

    @Override
    public void tick() {
        super.tick();

        if (dashSlashCooldown > 0) dashSlashCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 12.0);

            // Dash Slash from 4 to 8 blocks away
            double distSq = squaredDistanceTo(target);
            if (distSq >= 16.0 && distSq <= 64.0 && dashSlashCooldown <= 0 && this.isOnGround()) {
                dashSlashCooldown = 90;
                executeDashSlash(target);
            }
        }
    }

    private void executeDashSlash(LivingEntity target) {
        Vec3d dir = target.getPos().subtract(this.getPos()).normalize();
        this.setVelocity(dir.x * 1.3, 0.2, dir.z * 1.3);
        this.velocityModified = true;
        this.swingHand(Hand.MAIN_HAND);
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.2f, 1.4f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 5; i++) {
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 0.8, this.getZ(), 1, 0.2, 0.2, 0.2, 0.05);
            }
        }

        float dmg = (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.3);
        target.damage(this.getDamageSources().mobAttack(this), dmg);
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
