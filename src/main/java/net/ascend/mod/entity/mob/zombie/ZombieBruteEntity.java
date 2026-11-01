package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.ascend.mod.item.AscendItems;
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
import net.minecraft.world.World;

import java.util.List;

public class ZombieBruteEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieBruteEntity.class, TrackedDataHandlerRegistry.STRING);
    private int groundSlamCooldown = 0;
    private int slamTelegraphTicks = 0;

    public ZombieBruteEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 55.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR, 6.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6)
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
        return "Zombie Brute";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case E -> new ItemStack(Items.IRON_AXE);
            case D -> new ItemStack(Items.DIAMOND_AXE);
            case C -> new ItemStack(AscendItems.MYTHRIL_AXE);
            case B -> new ItemStack(AscendItems.MYTHRIL_AXE);
            case A -> new ItemStack(AscendItems.DRAGONITE_AXE);
            case S -> new ItemStack(AscendItems.VOIDSTEEL_AXE);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
    }

    @Override
    public void tick() {
        super.tick();

        if (groundSlamCooldown > 0) groundSlamCooldown--;

        if (slamTelegraphTicks > 0) {
            slamTelegraphTicks--;
            this.getNavigation().stop();

            if (this.getWorld() instanceof ServerWorld serverWorld) {
                // Expanding dust ring telegraph
                double radius = 3.5;
                RankedMobHelper.spawnTelegraphParticles(serverWorld, this.getX(), this.getY(), this.getZ(), radius);
            }

            if (slamTelegraphTicks == 0) {
                executeGroundSlam();
            }
            return;
        }

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 12.0);

            // Ground slam if within 4 blocks
            if (groundSlamCooldown <= 0 && squaredDistanceTo(target) < 16.0) {
                slamTelegraphTicks = 18; // 0.9s telegraph
                groundSlamCooldown = 160; // 8s cooldown
                this.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 0.8f, 1.4f);
            }
        }
    }

    private void executeGroundSlam() {
        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 3, 0.5, 0.2, 0.5, 0.1);
        }

        List<LivingEntity> victims = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(3.5, 1.5, 3.5),
                e -> e != this && !(e instanceof ZombieEntity) && e.isAlive()
        );

        float slamDmg = (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5);
        for (LivingEntity victim : victims) {
            victim.damage(this.getDamageSources().mobAttack(this), slamDmg);
            victim.addVelocity(0, 0.45, 0); // Knockup
            victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            victim.velocityModified = true;
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

