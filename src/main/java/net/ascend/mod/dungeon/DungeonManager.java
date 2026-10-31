package net.ascend.mod.dungeon;

import net.ascend.mod.AscendMod;
import net.ascend.mod.block.AscendBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class DungeonManager {
    public enum DungeonRank {
        E("E", 10, 0x777777),
        D("D", 20, 0x55FF55),
        C("C", 35, 0x5555FF),
        B("B", 50, 0xAA00AA),
        A("A", 75, 0xFF5555),
        S("S", 100, 0xFFAA00);

        public final String rankName;
        public final int recommendedLevel;
        public final int color;

        DungeonRank(String rankName, int recommendedLevel, int color) {
            this.rankName = rankName;
            this.recommendedLevel = recommendedLevel;
            this.color = color;
        }
    }

    public static void generateDungeonAt(ServerWorld world, BlockPos startPos, DungeonRank rank, ServerPlayerEntity player) {
        AscendMod.LOGGER.info("[Ascend] Generating {} Rank Dungeon at {}", rank.rankName, startPos);

        // 1. Generate Entrance Gate
        BlockPos current = startPos.down(20);
        buildRoom(world, current, 9, 6, 9, Blocks.DEEPSLATE_BRICKS.getDefaultState(), AscendBlocks.DUNGEON_GATE_BLOCK.getDefaultState());

        // 2. Generate 5 Sequential Challenge Rooms
        Random rand = new Random();
        for (int roomIndex = 1; roomIndex <= 5; roomIndex++) {
            current = current.add(14, 0, 0);
            int roomType = rand.nextInt(4); // 0 = Combat, 1 = Trap, 2 = Treasure, 3 = Mini-boss
            switch (roomType) {
                case 0 -> buildCombatRoom(world, current, rank);
                case 1 -> buildTrapRoom(world, current);
                case 2 -> buildTreasureRoom(world, current);
                default -> buildCombatRoom(world, current, rank);
            }
        }

        // 3. Generate Final Boss Room
        BlockPos bossRoom = current.add(18, 0, 0);
        buildBossArena(world, bossRoom, rank);

        world.playSound(null, startPos.getX(), startPos.getY(), startPos.getZ(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 1.0f, 0.8f);
        player.sendMessage(Text.literal("§6§l[DUNGEON LOCATED] §fA §eRank " + rank.rankName + " Dungeon§f gate has manifested nearby!"), false);
    }

    private static void buildRoom(ServerWorld world, BlockPos pos, int dx, int dy, int dz, net.minecraft.block.BlockState wall, net.minecraft.block.BlockState floor) {
        for (int x = -dx / 2; x <= dx / 2; x++) {
            for (int y = 0; y <= dy; y++) {
                for (int z = -dz / 2; z <= dz / 2; z++) {
                    BlockPos p = pos.add(x, y, z);
                    boolean isBorder = (x == -dx / 2 || x == dx / 2 || z == -dz / 2 || z == dz / 2 || y == dy);
                    if (y == 0) {
                        world.setBlockState(p, floor);
                    } else if (isBorder) {
                        world.setBlockState(p, wall);
                    } else {
                        world.setBlockState(p, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    private static void buildCombatRoom(ServerWorld world, BlockPos pos, DungeonRank rank) {
        buildRoom(world, pos, 13, 7, 13, Blocks.POLISHED_DEEPSLATE.getDefaultState(), Blocks.CHISELED_DEEPSLATE.getDefaultState());
    }

    private static void buildTrapRoom(ServerWorld world, BlockPos pos) {
        buildRoom(world, pos, 11, 6, 11, Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState(), Blocks.MAGMA_BLOCK.getDefaultState());
    }

    private static void buildTreasureRoom(ServerWorld world, BlockPos pos) {
        buildRoom(world, pos, 9, 6, 9, Blocks.DEEPSLATE_TILES.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState());
        world.setBlockState(pos.add(0, 1, 0), Blocks.CHEST.getDefaultState());
    }

    private static void buildBossArena(ServerWorld world, BlockPos pos, DungeonRank rank) {
        buildRoom(world, pos, 21, 10, 21, Blocks.OBSIDIAN.getDefaultState(), Blocks.CRYING_OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(0, 1, 0), AscendBlocks.BOSS_ALTAR.getDefaultState());
    }
}
