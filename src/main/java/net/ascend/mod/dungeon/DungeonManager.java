package net.ascend.mod.dungeon;

import net.ascend.mod.AscendMod;
import net.ascend.mod.block.AscendBlocks;
import net.ascend.mod.config.AscendConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class DungeonManager {

    public enum DungeonRank {
        E("E", 10, 0x888888, 150),
        D("D", 20, 0x55FF55, 300),
        C("C", 35, 0x5555FF, 700),
        B("B", 50, 0xAA00AA, 1500),
        A("A", 75, 0xFF5555, 3000),
        S("S", 100, 0xFFAA00, 6000);

        public final String rankName;
        public final int recommendedLevel;
        public final int color;
        public final int essenceBonus;

        DungeonRank(String rankName, int recommendedLevel, int color, int essenceBonus) {
            this.rankName = rankName;
            this.recommendedLevel = recommendedLevel;
            this.color = color;
            this.essenceBonus = essenceBonus;
        }
    }

    public enum DungeonTheme {
        GOBLIN_CAVE(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.DIRT),
        SHADOW_CRYPT(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CRYING_OBSIDIAN),
        CRYSTAL_MINES(Blocks.POLISHED_DEEPSLATE, Blocks.AMETHYST_BLOCK, Blocks.SMOOTH_BASALT),
        DRAGONS_NEST(Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.MAGMA_BLOCK),
        DEMON_CITADEL(Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, Blocks.CRYING_OBSIDIAN);

        public final BlockState wall;
        public final BlockState accent;
        public final BlockState floor;

        DungeonTheme(net.minecraft.block.Block wall, net.minecraft.block.Block accent, net.minecraft.block.Block floor) {
            this.wall = wall.getDefaultState();
            this.accent = accent.getDefaultState();
            this.floor = floor.getDefaultState();
        }
    }

    public static DungeonRank getRankFromDistance(double distance) {
        AscendConfig cfg = AscendConfig.INSTANCE;
        if (distance <= cfg.rankEDistance) return DungeonRank.E;
        if (distance <= cfg.rankDDistance) return DungeonRank.D;
        if (distance <= cfg.rankCDistance) return DungeonRank.C;
        if (distance <= cfg.rankBDistance) return DungeonRank.B;
        if (distance <= cfg.rankADistance) return DungeonRank.A;
        return DungeonRank.S;
    }

    public static void generateDungeonAt(ServerWorld world, BlockPos startPos, DungeonRank rank, ServerPlayerEntity player) {
        double distFromSpawn = Math.sqrt(startPos.getSquaredDistance(world.getSpawnPos()));
        DungeonRank actualRank = (rank != null) ? rank : getRankFromDistance(distFromSpawn);

        AscendMod.LOGGER.info("[Ascend] Generating {} Rank Dungeon (Distance: {}m) at {}", actualRank.rankName, (int) distFromSpawn, startPos);

        Random rand = new Random(startPos.asLong());

        // Select Theme based on rank or random
        DungeonTheme theme = switch (actualRank) {
            case E, D -> DungeonTheme.GOBLIN_CAVE;
            case C -> DungeonTheme.SHADOW_CRYPT;
            case B -> DungeonTheme.CRYSTAL_MINES;
            case A -> DungeonTheme.DRAGONS_NEST;
            case S -> DungeonTheme.DEMON_CITADEL;
        };

        // 1. Generate Surface Entrance & Shaft down to Y = -20
        int surfaceY = startPos.getY();
        int dungeonY = -20;
        buildEntranceShaft(world, startPos, surfaceY, dungeonY, theme);

        BlockPos current = new BlockPos(startPos.getX(), dungeonY, startPos.getZ());

        // 2. Room 0: Entrance Hall
        buildRoom(world, current, 9, 6, 9, theme);
        world.setBlockState(current.add(0, 1, 0), AscendBlocks.DUNGEON_GATE_BLOCK.getDefaultState());

        // 3. Generate EXACTLY 5 Challenge Rooms in a connected linear chain
        int[] roomTypes = pick5UniqueRoomTypes(rand);

        for (int i = 0; i < 5; i++) {
            // Build connecting corridor (length 8)
            buildCorridor(world, current, 8, Direction.SOUTH, theme);
            current = current.offset(Direction.SOUTH, 8);

            // Room size depends on type
            int type = roomTypes[i];
            buildSpecificRoom(world, current, type, theme, actualRank, rand);

            // Announce room entry marker
            current = current.offset(Direction.SOUTH, 6);
        }

        // 4. Final Corridor & Boss Room
        buildCorridor(world, current, 8, Direction.SOUTH, theme);
        current = current.offset(Direction.SOUTH, 8);
        buildBossRoom(world, current, actualRank, theme);

        // Sound & feedback
        world.playSound(null, startPos, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 1.2f, 0.7f);
        if (player != null) {
            player.sendMessage(Text.literal("§6§l[DUNGEON MANIFESTED] §eRank " + actualRank.rankName + " Dungeon §fwith §b5 Challenge Rooms §funlocked below!"), false);
        }
    }

    private static int[] pick5UniqueRoomTypes(Random rand) {
        int[] available = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        // Shuffle first 5
        for (int i = 0; i < 5; i++) {
            int swap = i + rand.nextInt(10 - i);
            int tmp = available[i];
            available[i] = available[swap];
            available[swap] = tmp;
        }
        return new int[]{available[0], available[1], available[2], available[3], available[4]};
    }

    private static void buildSpecificRoom(ServerWorld world, BlockPos center, int type, DungeonTheme theme, DungeonRank rank, Random rand) {
        switch (type) {
            case 0 -> { // 1. Combat Arena
                buildRoom(world, center, 13, 7, 13, theme);
                spawnDungeonMob(world, center.add(-2, 1, -2));
                spawnDungeonMob(world, center.add(2, 1, 2));
            }
            case 1 -> { // 2. Trap Corridor
                buildRoom(world, center, 11, 5, 11, theme);
                for (int x = -3; x <= 3; x++) {
                    world.setBlockState(center.add(x, 0, 0), Blocks.MAGMA_BLOCK.getDefaultState());
                }
            }
            case 2 -> { // 3. Puzzle Room
                buildRoom(world, center, 11, 6, 11, theme);
                world.setBlockState(center.add(0, 1, 3), Blocks.LEVER.getDefaultState());
                world.setBlockState(center.add(0, 1, -3), Blocks.REDSTONE_LAMP.getDefaultState());
            }
            case 3 -> { // 4. Treasure Room
                buildRoom(world, center, 9, 5, 9, theme);
                world.setBlockState(center.add(0, 0, 0), Blocks.GOLD_BLOCK.getDefaultState());
                world.setBlockState(center.add(0, 1, 0), Blocks.CHEST.getDefaultState());
            }
            case 4 -> { // 5. Mini-boss Room
                buildRoom(world, center, 13, 8, 13, theme);
                world.setBlockState(center.add(0, 1, 0), Blocks.CHISELED_DEEPSLATE.getDefaultState());
                spawnDungeonMob(world, center.add(0, 2, 0));
            }
            case 5 -> { // 6. Dark Hall
                buildRoom(world, center, 9, 6, 15, theme);
                world.setBlockState(center.add(0, 2, 0), Blocks.COBWEB.getDefaultState());
            }
            case 6 -> { // 7. Bridge over Lava
                buildRoom(world, center, 13, 8, 13, theme);
                for (int x = -5; x <= 5; x++) {
                    for (int z = -5; z <= 5; z++) {
                        if (x != 0) {
                            world.setBlockState(center.add(x, 0, z), Blocks.LAVA.getDefaultState());
                        }
                    }
                }
            }
            case 7 -> { // 8. Crystal Caverns Room
                buildRoom(world, center, 11, 7, 11, theme);
                for (int i = 0; i < 4; i++) {
                    world.setBlockState(center.add(rand.nextInt(5)-2, 1, rand.nextInt(5)-2), AscendBlocks.MANA_CRYSTAL_ORE.getDefaultState());
                }
            }
            case 8 -> { // 9. Ambush Room
                buildRoom(world, center, 11, 6, 11, theme);
                spawnDungeonMob(world, center.add(3, 1, 3));
                spawnDungeonMob(world, center.add(-3, 1, -3));
            }
            default -> { // 10. Rest & Shrine Room
                buildRoom(world, center, 9, 6, 9, theme);
                world.setBlockState(center.add(0, 0, 0), Blocks.SEA_LANTERN.getDefaultState());
                world.setBlockState(center.add(0, 1, 0), Blocks.WATER.getDefaultState());
            }
        }
    }

    private static void buildBossRoom(ServerWorld world, BlockPos pos, DungeonRank rank, DungeonTheme theme) {
        buildRoom(world, pos, 21, 10, 21, theme);
        // Boss Altar at center
        world.setBlockState(pos.add(0, 1, 0), AscendBlocks.BOSS_ALTAR.getDefaultState());

        // Soul fire braziers
        for (int[] offset : new int[][]{{-6, -6}, {6, -6}, {-6, 6}, {6, 6}}) {
            world.setBlockState(pos.add(offset[0], 1, offset[1]), Blocks.OBSIDIAN.getDefaultState());
            world.setBlockState(pos.add(offset[0], 2, offset[1]), Blocks.SOUL_FIRE.getDefaultState());
        }

        // Locked Iron Gates at entrance
        for (int y = 1; y <= 3; y++) {
            world.setBlockState(pos.add(-1, y, -10), Blocks.IRON_BARS.getDefaultState());
            world.setBlockState(pos.add(0, y, -10), Blocks.IRON_BARS.getDefaultState());
            world.setBlockState(pos.add(1, y, -10), Blocks.IRON_BARS.getDefaultState());
        }
    }

    private static void buildEntranceShaft(ServerWorld world, BlockPos startPos, int topY, int bottomY, DungeonTheme theme) {
        for (int y = bottomY; y <= topY; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos p = new BlockPos(startPos.getX() + x, y, startPos.getZ() + z);
                    if (x == 0 && z == 0) {
                        world.setBlockState(p, Blocks.LADDER.getDefaultState());
                    } else {
                        world.setBlockState(p, theme.wall);
                    }
                }
            }
        }
    }

    private static void buildRoom(ServerWorld world, BlockPos pos, int dx, int dy, int dz, DungeonTheme theme) {
        for (int x = -dx / 2; x <= dx / 2; x++) {
            for (int y = 0; y <= dy; y++) {
                for (int z = -dz / 2; z <= dz / 2; z++) {
                    BlockPos p = pos.add(x, y, z);
                    boolean isBorder = (x == -dx / 2 || x == dx / 2 || z == -dz / 2 || z == dz / 2 || y == dy);
                    if (y == 0) {
                        world.setBlockState(p, theme.floor);
                    } else if (isBorder) {
                        world.setBlockState(p, (x + z) % 3 == 0 ? theme.accent : theme.wall);
                    } else {
                        world.setBlockState(p, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    private static void buildCorridor(ServerWorld world, BlockPos from, int length, Direction dir, DungeonTheme theme) {
        for (int step = 1; step <= length; step++) {
            BlockPos mid = from.offset(dir, step);
            for (int w = -1; w <= 1; w++) {
                for (int h = 0; h <= 4; h++) {
                    BlockPos p = (dir.getAxis() == Direction.Axis.Z) ? mid.add(w, h, 0) : mid.add(0, h, w);
                    if (h == 0) {
                        world.setBlockState(p, theme.floor);
                    } else if (h == 4 || w == -1 || w == 1) {
                        world.setBlockState(p, theme.wall);
                    } else {
                        world.setBlockState(p, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    private static void spawnDungeonMob(ServerWorld world, BlockPos pos) {
        ZombieEntity mob = EntityType.ZOMBIE.create(world);
        if (mob != null) {
            mob.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            world.spawnEntity(mob);
        }
    }
}
