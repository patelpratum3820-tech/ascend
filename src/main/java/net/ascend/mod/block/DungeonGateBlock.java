package net.ascend.mod.block;

import net.ascend.mod.dungeon.DungeonManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DungeonGateBlock extends Block {
    public DungeonGateBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld sw = (ServerWorld) world;

            // Generate the ranked underground dungeon if not already generated
            DungeonManager.generateDungeonAt(sw, pos, null, serverPlayer);

            // Teleport player down the ladder into the entrance hall (y = -20)
            serverPlayer.teleport(pos.getX() + 0.5, -19.0, pos.getZ() + 0.5);
            sw.playSound(null, pos.getX(), -19, pos.getZ(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 1.0f, 1.2f);
            serverPlayer.sendMessage(Text.literal("§d✦ You descended into the Dungeon! Prepare for battle."), true);
        }
        return ActionResult.SUCCESS;
    }
}

