package net.ascend.mod.entity.boss;

import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AscendBossEntity extends HostileEntity {
    private final ServerBossBar bossBar;
    private final String bossTitle;
    private final int totalPhases;
    private int currentPhase = 1;
    private final long baseEssenceDrop;

    public AscendBossEntity(EntityType<? extends HostileEntity> entityType, World world, String bossTitle, BossBar.Color barColor, int totalPhases, long baseEssenceDrop) {
        super(entityType, world);
        this.bossTitle = bossTitle;
        this.totalPhases = totalPhases;
        this.baseEssenceDrop = baseEssenceDrop;
        this.bossBar = (ServerBossBar) new ServerBossBar(Text.literal(bossTitle), barColor, BossBar.Style.NOTCHED_10).setDarkenSky(true);
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
    public void tick() {
        super.tick();
        if (!getWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Phase Transition Check
            float healthFraction = this.getHealth() / this.getMaxHealth();
            if (totalPhases >= 2 && currentPhase == 1 && healthFraction <= 0.5f) {
                currentPhase = 2;
                onPhaseTransition(2);
            } else if (totalPhases >= 3 && currentPhase == 2 && healthFraction <= 0.25f) {
                currentPhase = 3;
                onPhaseTransition(3);
            }
        }
    }

    protected void onPhaseTransition(int newPhase) {
        if (!getWorld().isClient()) {
            ServerWorld sw = (ServerWorld) getWorld();
            sw.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1, getZ(), 2, 0, 0, 0, 0);
            sw.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.5f, 0.8f);
            this.bossBar.setName(Text.literal("§c§l" + bossTitle + " §e(Phase " + newPhase + ")"));
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!getWorld().isClient()) {
            this.bossBar.clearPlayers();

            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                // Reward scaled Essence
                data.addEssenceWithBonus(baseEssenceDrop);
                player.sendMessage(Text.literal("§6§l[BOSS DEFEATED] §fReceived §b+" + baseEssenceDrop + " Essence§f!"), false);

                // Boss Extraction for Summoner Class
                if ("Summoner".equalsIgnoreCase(data.getPlayerClass())) {
                    player.sendMessage(Text.literal("§d§l[SHADOW EXTRACTION] §fYou extracted the boss's soul essence!"), false);
                    player.giveItemStack(new ItemStack(AscendItems.SHADOW_ESSENCE, 3));
                }
            }

            // Drop guaranteed rare materials
            dropItem(AscendItems.VOIDSTEEL_INGOT);
            dropItem(AscendItems.DRAGON_SCALE);
            dropItem(AscendItems.DUNGEON_MAP);
        }
    }
}

