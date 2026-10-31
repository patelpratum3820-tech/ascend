package net.ascend.mod.block;

import net.ascend.mod.entity.AscendEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BossAltarBlock extends Block {
    public BossAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            ServerWorld sw = (ServerWorld) world;

            // Spawn dramatic summoning effects
            sw.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 3, 0, 0, 0, 0);
            sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 50, 1.0, 1.0, 1.0, 0.1);
            sw.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.5f, 0.7f);

            // Determine boss to spawn based on height or random choice
            int roll = sw.random.nextInt(4);
            net.minecraft.entity.mob.MobEntity boss = switch (roll) {
                case 0 -> AscendEntities.GOBLIN_KING.create(sw);
                case 1 -> AscendEntities.SHADOW_KNIGHT.create(sw);
                case 2 -> AscendEntities.CRYSTAL_GOLEM.create(sw);
                default -> AscendEntities.VOID_LICH.create(sw);
            };

            if (boss != null) {
                boss.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, 0);
                sw.spawnEntity(boss);
                player.sendMessage(Text.literal("§4§l[BOSS AWAKENED] §c" + boss.getName().getString() + " §fhas emerged!"), false);
            }
        }
        return ActionResult.SUCCESS;
    }
}
