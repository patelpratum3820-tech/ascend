package net.ascend.mod.entity.mob.skeleton;

import net.ascend.mod.entity.AscendEntities;
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
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class SkeletonCommanderEntity extends SkeletonEntity implements AscendRankedMob {
    private static final TrackedData<String> RANK_DATA = DataTracker.registerData(SkeletonCommanderEntity.class, TrackedDataHandlerRegistry.STRING);
    private final ServerBossBar bossBar;
    private boolean summonedSquad = false;

    public SkeletonCommanderEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = new ServerBossBar(
                Text.literal("§c§lSkeleton Commander"),
                BossBar.Color.WHITE,
                BossBar.Style.PROGRESS
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 110.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 13.0)
                .add(EntityAttributes.GENERIC_ARMOR, 12.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6);
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
        this.bossBar.setName(Text.literal("§c§l[" + rank.getFormatting() + "Rank " + rank.getLabel() + "§c§l] Skeleton Commander"));
    }

    @Override
    public String getMobBaseName() {
        return "Skeleton Commander";
    }

    private void equipWeaponsForRank(MobRank rank) {
        ItemStack weapon = switch (rank) {
            case A, S -> new ItemStack(AscendItems.VOIDSTEEL_SWORD);
            case B -> new ItemStack(AscendItems.DRAGONITE_SWORD);
            default -> new ItemStack(AscendItems.MYTHRIL_KATANA);
        };
        this.equipStack(EquipmentSlot.MAINHAND, weapon);
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.WHITE_BANNER));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                RankedMobHelper.alertNearbyAllies(this, target, 20.0);

                if (!summonedSquad) {
                    summonedSquad = true;
                    summonSquad();
                }
            }

            // Commander Aura: Buffs all nearby skeletons every 3 seconds
            if (this.age % 60 == 0 && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY() + 2.0, this.getZ(), 8, 0.4, 0.4, 0.4, 0.05);

                List<SkeletonEntity> minions = serverWorld.getEntitiesByClass(
                        SkeletonEntity.class,
                        this.getBoundingBox().expand(14.0),
                        s -> s.isAlive() && s != this
                );

                for (SkeletonEntity minion : minions) {
                    minion.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 100, 1));
                    minion.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 1));
                    serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, minion.getX(), minion.getY() + 1.2, minion.getZ(), 2, 0.2, 0.2, 0.2, 0.02);
                }
            }
        }
    }

    private void summonSquad() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        this.playSound(SoundEvents.EVENT_RAID_HORN.value(), 1.4f, 1.0f);

        // Summon 1 Archer and 2 Swordsmen
        SkeletonArcherEntity archer = new SkeletonArcherEntity(AscendEntities.SKELETON_ARCHER, this.getWorld());
        archer.setPosition(this.getX() - 2.5, this.getY(), this.getZ());
        archer.setMobRank(MobRank.C);
        archer.setTarget(this.getTarget());
        serverWorld.spawnEntity(archer);

        for (int i = 0; i < 2; i++) {
            SkeletonSwordsmanEntity sword = new SkeletonSwordsmanEntity(AscendEntities.SKELETON_SWORDSMAN, this.getWorld());
            sword.setPosition(this.getX() + (i * 2 - 1) * 2.0, this.getY(), this.getZ() + 2.0);
            sword.setMobRank(MobRank.C);
            sword.setTarget(this.getTarget());
            serverWorld.spawnEntity(sword);
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
        nbt.putBoolean("SummonedSquad", summonedSquad);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AscendRank")) {
            setMobRank(MobRank.fromString(nbt.getString("AscendRank")));
        }
        this.summonedSquad = nbt.getBoolean("SummonedSquad");
    }
}
