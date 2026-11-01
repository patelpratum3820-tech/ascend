package net.ascend.mod.entity.mob.skeleton;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
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

import java.util.List;

public class BoneReaperEntity extends SkeletonEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(BoneReaperEntity.class, TrackedDataHandlerRegistry.STRING);
    private int spinCooldown = 0;
    private int grappleCooldown = 0;

    public BoneReaperEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 38.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
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
        return "Bone Reaper";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case A, S -> new ItemStack(Items.NETHERITE_HOE);
            case B, C -> new ItemStack(Items.DIAMOND_HOE);
            default -> new ItemStack(Items.IRON_HOE);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
    }

    @Override
    public void tick() {
        super.tick();

        if (spinCooldown > 0) spinCooldown--;
        if (grappleCooldown > 0) grappleCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 14.0);

            double distSq = squaredDistanceTo(target);

            // Grapple pull from 6 to 12 blocks away
            if (distSq >= 36.0 && distSq <= 144.0 && grappleCooldown <= 0) {
                grappleCooldown = 140;
                executeGrapple(target);
            }

            // 360 Spin attack when close (< 3.5 blocks)
            if (distSq < 12.25 && spinCooldown <= 0) {
                spinCooldown = 100;
                execute360Spin();
            }
        }
    }

    private void executeGrapple(LivingEntity target) {
        this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, 1.2f, 0.8f);

        Vec3d pull = this.getPos().subtract(target.getPos()).normalize().multiply(1.2);
        target.setVelocity(pull.x, 0.35, pull.z);
        target.velocityModified = true;

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1.0, target.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
        }
    }

    private void execute360Spin() {
        this.swingHand(Hand.MAIN_HAND);
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 0.8f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 16; i++) {
                double angle = (i / 16.0) * Math.PI * 2;
                double px = this.getX() + Math.cos(angle) * 2.2;
                double pz = this.getZ() + Math.sin(angle) * 2.2;
                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, px, this.getY() + 0.8, pz, 1, 0, 0, 0, 0);
            }
        }

        List<LivingEntity> victims = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(2.5, 1.0, 2.5),
                e -> e != this && !(e instanceof SkeletonEntity) && e.isAlive()
        );

        float spinDmg = (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.4);
        for (LivingEntity victim : victims) {
            victim.damage(this.getDamageSources().mobAttack(this), spinDmg);
            victim.takeKnockback(1.0, this.getX() - victim.getX(), this.getZ() - victim.getZ());
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
