package net.ascend.mod.entity.boss;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class CrystalGolemEntity extends IronGolemEntity {
    private final ServerBossBar bossBar;
    private int slamCooldown = 120;

    public CrystalGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar) new ServerBossBar(Text.literal("Crystal Golem"), BossBar.Color.BLUE, BossBar.Style.NOTCHED_10).setDarkenSky(true);
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
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!getWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            if (getTarget() != null) {
                slamCooldown--;
                if (slamCooldown <= 0 && distanceTo(getTarget()) <= 6.0) {
                    slamCooldown = 140;
                    if (getWorld() instanceof ServerWorld sw) {
                        sw.spawnParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 3, 0.5, 0.2, 0.5, 0);
                        sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 40, 3.0, 0.2, 3.0, 0.1);
                        sw.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, SoundCategory.HOSTILE, 1.8f, 0.6f);
                        getTarget().damage(getDamageSources().mobAttack(this), 22.0f);
                        getTarget().addVelocity(0, 0.6, 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!getWorld().isClient()) {
            this.bossBar.clearPlayers();
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                data.addEssenceWithBonus(1200);
                player.sendMessage(Text.literal("§6§l[BOSS DEFEATED] §fReceived §b+1200 Essence§f!"), false);

                if ("Summoner".equalsIgnoreCase(data.getPlayerClass())) {
                    player.sendMessage(Text.literal("§d§l[SHADOW EXTRACTION] §fExtracted Crystal Core!"), false);
                    player.giveItemStack(new ItemStack(AscendItems.SHADOW_ESSENCE, 3));
                }
            }
            dropItem(AscendItems.MYTHRIL_INGOT);
            dropItem(AscendItems.MANA_CRYSTAL);
        }
    }
}
