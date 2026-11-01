package net.ascend.mod.story;

import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class DivineAwakening {

    public static final int SHRINE_X = 0;
    public static final int SHRINE_Y = 220;
    public static final int SHRINE_Z = 0;

    public static void initiateAwakening(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // 1. Build the grand Temple of the Chosen platform matching reference art
        buildCelestialPlatform(world);

        // 2. Safe positioning inside the Temple Altar
        player.teleport(world, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 5.5, 0f, 0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 4, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2400, 0, false, false));

        // 3. Celestial audio & visuals
        world.playSound(null, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 0.5, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT, 2.0f, 0.9f);
        world.playSound(null, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 0.5, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.2f, 1.4f);
        world.spawnParticles(ParticleTypes.END_ROD, SHRINE_X + 0.5, SHRINE_Y + 2.0, SHRINE_Z + 0.5, 100, 3.0, 2.0, 3.0, 0.05);
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, SHRINE_X + 0.5, SHRINE_Y + 2.0, SHRINE_Z + 0.5, 80, 2.5, 1.5, 2.5, 0.1);

        // 4. Divine Title
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 50, 20));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§e§l✦ TEMPLE OF THE CHOSEN ✦")));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("§bA Goddess summons you from across worlds...")));

        // 5. Open Awakening Screen (which displays the Goddess Prologue first)
        AscendPackets.sendOpenCreationScreen(player);
    }

    public static void completeAwakening(ServerPlayerEntity player, PlayerData data) {
        ServerWorld world = player.getServerWorld();

        // 1. Goddess farewell message
        player.sendMessage(Text.literal("§e═══════════════════════════════════════════════════"), false);
        player.sendMessage(Text.literal("§6§l[Goddess of Rebirth] §a\"The divine pact is sealed! Descend now, Ascender, and let your legend shake the heavens!\""), false);
        player.sendMessage(Text.literal("§e═══════════════════════════════════════════════════"), false);

        // 2. Triumphant fanfares & particles
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 2.0f, 1.0f);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.5f, 0.8f);
        world.spawnParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 4, 0, 0, 0, 0);
        world.spawnParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 2.0, player.getZ(), 50, 1.5, 1.5, 1.5, 0.1);

        // 3. Find safe surface landing position
        int targetX = SHRINE_X + 24;
        int targetZ = SHRINE_Z + 24;
        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, targetX, targetZ);
        if (topY <= world.getBottomY()) topY = 70;

        // 4. Teleport down to the Overworld
        player.teleport(world, targetX + 0.5, topY + 1.0, targetZ + 0.5, player.getYaw(), 0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 4, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0, false, false));

        // 5. Give starter kit
        giveStarterGear(player, data.getPlayerClass());

        // Surface arrival particles
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, targetX + 0.5, topY + 1.5, targetZ + 0.5, 60, 1.0, 1.5, 1.0, 0.1);
        world.playSound(null, targetX + 0.5, topY + 1.0, targetZ + 0.5, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.2f);
    }

    private static void giveStarterGear(ServerPlayerEntity player, String pClass) {
        player.getInventory().offerOrDrop(new ItemStack(Items.BREAD, 16));
        switch (pClass.toLowerCase()) {
            case "swordsman" -> player.getInventory().offerOrDrop(new ItemStack(Items.IRON_SWORD));
            case "archer" -> {
                player.getInventory().offerOrDrop(new ItemStack(Items.BOW));
                player.getInventory().offerOrDrop(new ItemStack(Items.ARROW, 64));
            }
            case "tank" -> {
                player.getInventory().offerOrDrop(new ItemStack(Items.IRON_SWORD));
                player.getInventory().offerOrDrop(new ItemStack(Items.SHIELD));
            }
            case "mage", "summoner" -> {
                player.getInventory().offerOrDrop(new ItemStack(Items.GOLDEN_SWORD));
                player.getInventory().offerOrDrop(new ItemStack(Items.ENDER_PEARL, 4));
            }
            default -> player.getInventory().offerOrDrop(new ItemStack(Items.IRON_SWORD));
        }
    }

    private static void buildCelestialPlatform(ServerWorld world) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        // 1. Inverted rock island underside (Deepslate / Blackstone / Tuff cone)
        for (int y = SHRINE_Y - 1; y >= SHRINE_Y - 9; y--) {
            int layerRadius = (int) (11.0 * (1.0 - (double) (SHRINE_Y - y) / 10.0));
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    double dist = Math.hypot(dx, dz);
                    if (dist <= layerRadius + 0.4) {
                        pos.set(SHRINE_X + dx, y, SHRINE_Z + dz);
                        if (y == SHRINE_Y - 1) {
                            world.setBlockState(pos, Blocks.COBBLED_DEEPSLATE.getDefaultState());
                        } else if (dist > layerRadius * 0.7) {
                            world.setBlockState(pos, Blocks.POLISHED_BLACKSTONE.getDefaultState());
                        } else {
                            world.setBlockState(pos, Blocks.DEEPSLATE.getDefaultState());
                        }
                    }
                }
            }
        }

        // 2. Temple Courtyard Surface (Y = SHRINE_Y)
        int platformRadius = 11;
        for (int dx = -platformRadius; dx <= platformRadius; dx++) {
            for (int dz = -platformRadius; dz <= platformRadius; dz++) {
                double dist = Math.hypot(dx, dz);
                if (dist <= platformRadius + 0.2) {
                    pos.set(SHRINE_X + dx, SHRINE_Y, SHRINE_Z + dz);

                    // Outer perimeter golden trim
                    if (dist >= platformRadius - 0.9) {
                        world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
                    }
                    // Central blue glowing reflection pool (-2..2 in X, -2..2 in Z)
                    else if (Math.abs(dx) <= 2 && Math.abs(dz) <= 2) {
                        world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
                    }
                    // Pool border: Chiseled quartz
                    else if (Math.abs(dx) <= 3 && Math.abs(dz) <= 3) {
                        world.setBlockState(pos, Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState());
                    }
                    // Inner ring: Sea Lantern floor rune
                    else if (Math.abs(dist - 6.5) < 0.7) {
                        world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
                    }
                    // Main courtyard: Smooth Quartz
                    else {
                        world.setBlockState(pos, Blocks.SMOOTH_QUARTZ.getDefaultState());
                    }
                }
            }
        }

        // 3. Central pool water layer (Y = SHRINE_Y + 1)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                pos.set(SHRINE_X + dx, SHRINE_Y + 1, SHRINE_Z + dz);
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
            }
        }

        // 4. Grand Altar of the Goddess (North side, dz = -7 to -9)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -9; dz <= -7; dz++) {
                pos.set(SHRINE_X + dx, SHRINE_Y + 1, SHRINE_Z + dz);
                world.setBlockState(pos, Blocks.SMOOTH_QUARTZ.getDefaultState());
            }
        }
        // Dais step
        for (int dx = -2; dx <= 2; dx++) {
            pos.set(SHRINE_X + dx, SHRINE_Y + 2, SHRINE_Z - 8);
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
        }
        // Goddess Beacon / Altar center
        pos.set(SHRINE_X, SHRINE_Y + 3, SHRINE_Z - 8);
        world.setBlockState(pos, Blocks.BEACON.getDefaultState());
        pos.set(SHRINE_X, SHRINE_Y + 4, SHRINE_Z - 8);
        world.setBlockState(pos, Blocks.AMETHYST_CLUSTER.getDefaultState());

        // 5. Grand Colonnade (8 Quartz Pillars with Golden Capitals and End Rods)
        int[][] pillarCoords = {
                {-6, -6}, {0, -8}, {6, -6},
                {-8, 0},           {8, 0},
                {-6, 6},  {0, 8},  {6, 6}
        };

        for (int[] p : pillarCoords) {
            // Pillar base
            pos.set(SHRINE_X + p[0], SHRINE_Y + 1, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState());

            // Shaft
            for (int dy = 2; dy <= 6; dy++) {
                pos.set(SHRINE_X + p[0], SHRINE_Y + dy, SHRINE_Z + p[1]);
                world.setBlockState(pos, Blocks.QUARTZ_PILLAR.getDefaultState());
            }

            // Golden capital & sea lantern lamp
            pos.set(SHRINE_X + p[0], SHRINE_Y + 7, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
            pos.set(SHRINE_X + p[0], SHRINE_Y + 8, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
            pos.set(SHRINE_X + p[0], SHRINE_Y + 9, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.END_ROD.getDefaultState());
        }

        // 6. Grand South Staircase (dz = 10 to 14)
        for (int i = 0; i <= 4; i++) {
            int sz = 10 + i;
            int sy = SHRINE_Y - i;
            for (int sx = -2; sx <= 2; sx++) {
                pos.set(SHRINE_X + sx, sy, SHRINE_Z + sz);
                world.setBlockState(pos, Blocks.SMOOTH_QUARTZ_STAIRS.getDefaultState());
            }
            // Railings
            pos.set(SHRINE_X - 3, sy + 1, SHRINE_Z + sz);
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
            pos.set(SHRINE_X + 3, sy + 1, SHRINE_Z + sz);
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
        }
    }
}
