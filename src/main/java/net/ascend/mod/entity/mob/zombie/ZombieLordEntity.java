package net.ascend.mod.entity.mob.zombie;

import net.ascend.mod.entity.AscendEntities;
import net.ascend.mod.entity.mob.base.AscendRankedMob;
import net.ascend.mod.entity.mob.base.MobRank;
import net.ascend.mod.entity.mob.base.RankedMobHelper;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ZombieLordEntity extends ZombieEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(ZombieLordEntity.class, TrackedDataHandlerRegistry.STRING);
    private final ServerBossBar bossBar;
    private int rallyRoarCooldown = 100;
    private boolean summonedGuards = false;

    public ZombieLordEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = new ServerBossBar(
                Text.literal("§c§lZombie Lord"),
                BossBar.Color.PURPLE,
                BossBar.Style.PROGRESS
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.26)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 14.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RANK_DATA, "B");
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
        this.bossBar.setName(Text.literal("§c§l[" + rank.getFormatting() + "Rank " + rank.getLabel() + "§c§l] Zombie Lord"));
    }

    @Override
    public String getMobBaseName() {
        return "Zombie Lord";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case A, S -> new ItemStack(AscendItems.DRAGONITE_GREATSWORD);
            default -> new ItemStack(AscendItems.MYTHRIL_KATANA);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            if (rallyRoarCooldown > 0) rallyRoarCooldown--;

            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                RankedMobHelper.alertNearbyAllies(this, target, 20.0);

                // Rallying roar: buffs all nearby undead
                if (rallyRoarCooldown <= 0) {
                    rallyRoarCooldown = 200; // 10s
                    performRallyRoar();
                }

                // Guard summons at 50% HP
                if (!summonedGuards && this.getHealth() < this.getMaxHealth() * 0.5f) {
                    summonedGuards = true;
                    summonGuardHorde();
                }
            }
        }
    }

    private void performRallyRoar() {
        this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 1.4f);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 1.5, this.getZ(), 1, 0, 0, 0, 0);

            List<ZombieEntity> allies = serverWorld.getEntitiesByClass(
                    ZombieEntity.class,
                    this.getBoundingBox().expand(16.0),
                    z -> z.isAlive()
            );

            for (ZombieEntity ally : allies) {
                ally.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 160, 1));
                ally.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 160, 1));
                serverWorld.spawnParticles(ParticleTypes.HEART, ally.getX(), ally.getY() + 1.0, ally.getZ(), 3, 0.2, 0.2, 0.2, 0.05);
            }
        }
    }

    private void summonGuardHorde() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        this.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1.0f, 1.2f);
        for (int i = 0; i < 3; i++) {
            ZombieSoldierEntity guard = new ZombieSoldierEntity(AscendEntities.ZOMBIE_SOLDIER, this.getWorld());
            guard.setPosition(this.getX() + (i - 1) * 2.0, this.getY(), this.getZ() + (this.random.nextDouble() - 0.5) * 3.0);
            guard.setMobRank(MobRank.C);
            guard.setTarget(this.getTarget());
            serverWorld.spawnEntity(guard);
            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, guard.getX(), guard.getY() + 1.0, guard.getZ(), 15, 0.3, 0.5, 0.3, 0.05);
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.bossBar.clearPlayers();
        RankedMobHelper.handleDrops(this, getMobRank(), damageSource);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("AscendRank", getMobRank().name());
        nbt.putBoolean("SummonedGuards", summonedGuards);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AscendRank")) {
            setMobRank(MobRank.fromString(nbt.getString("AscendRank")));
        }
        this.summonedGuards = nbt.getBoolean("SummonedGuards");
    }
}

