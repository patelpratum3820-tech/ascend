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
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ZombieBerserkerEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieBerserkerEntity.class, TrackedDataHandlerRegistry.STRING);
    private boolean isEnraged = false;

    public ZombieBerserkerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
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
        return "Zombie Berserker";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case E -> new ItemStack(Items.STONE_AXE);
            case D -> new ItemStack(Items.IRON_AXE);
            case C -> new ItemStack(AscendItems.MYTHRIL_AXE);
            case B -> new ItemStack(AscendItems.MYTHRIL_AXE);
            case A -> new ItemStack(AscendItems.DRAGONITE_AXE);
            case S -> new ItemStack(AscendItems.VOIDSTEEL_AXE);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon.copy());
        this.equipStack(EquipmentSlot.OFFHAND, weapon.copy());
    }

    @Override
    public void tick() {
        super.tick();

        // Enrage check below 40% HP
        if (!this.getWorld().isClient) {
            if (!isEnraged && this.getHealth() < this.getMaxHealth() * 0.40f) {
                isEnraged = true;
                this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.2f, 1.5f);
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1));
            }

            if (isEnraged && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 1.2, this.getZ(), 1, 0.2, 0.2, 0.2, 0.01);
            }

            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                RankedMobHelper.alertNearbyAllies(this, target, 10.0);
            }
        }
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit) {
            // Dual-wield extra swing
            this.swingHand(Hand.OFF_HAND);
            if (isEnraged && target instanceof LivingEntity livingTarget) {
                livingTarget.takeKnockback(0.4, this.getX() - livingTarget.getX(), this.getZ() - livingTarget.getZ());
            }
        }
        return hit;
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

