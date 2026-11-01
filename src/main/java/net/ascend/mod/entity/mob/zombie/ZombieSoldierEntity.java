package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class ZombieSoldierEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieSoldierEntity.class, TrackedDataHandlerRegistry.STRING);
    private int shieldBashCooldown = 0;
    private int blockCooldown = 0;

    public ZombieSoldierEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
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
        return "Zombie Soldier";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case E -> new ItemStack(Items.STONE_SWORD);
            case D -> new ItemStack(Items.IRON_SWORD);
            case C -> new ItemStack(AscendItems.MYTHRIL_SWORD);
            case B -> new ItemStack(AscendItems.MYTHRIL_KATANA);
            case A -> new ItemStack(AscendItems.DRAGONITE_SWORD);
            case S -> new ItemStack(AscendItems.VOIDSTEEL_SWORD);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
        this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
    }

    @Override
    public void tick() {
        super.tick();

        if (shieldBashCooldown > 0) shieldBashCooldown--;
        if (blockCooldown > 0) blockCooldown--;

        LivingEntity target = getTarget();
        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            RankedMobHelper.alertNearbyAllies(this, target, 12.0);

            // Telegraphed Shield Bash when in close range (every ~100 ticks)
            double distSq = squaredDistanceTo(target);
            if (distSq < 9.0 && shieldBashCooldown <= 0) {
                performShieldBash(target);
            }
        }
    }

    private void performShieldBash(LivingEntity target) {
        shieldBashCooldown = 120;
        this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.2f, 0.8f);

        // Visual telegraph & bash
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0, target.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
        }

        // Damage & Knockback
        float dmg = (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.2f);
        target.damage(this.getDamageSources().mobAttack(this), dmg);
        target.takeKnockback(1.5, this.getX() - target.getX(), this.getZ() - target.getZ());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Chance to block attacks with shield
        if (!source.isIndirect() && blockCooldown <= 0 && source.getAttacker() instanceof LivingEntity attacker) {
            double dot = this.getRotationVector().dotProduct(attacker.getPos().subtract(this.getPos()).normalize());
            if (dot > 0.0) { // Facing attacker
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                this.swingHand(Hand.OFF_HAND);
                blockCooldown = 40;
                return super.damage(source, amount * 0.25f); // 75% damage reduction on block
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

