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

    private static final int SHRINE_X = 0;
    private static final int SHRINE_Y = 220;
    private static final int SHRINE_Z = 0;

    public static void initiateAwakening(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // 1. Build celestial platform if not yet present
        buildCelestialPlatform(world);

        // 2. Safe positioning in the Astral Shrine
        player.teleport(world, SHRINE_X + 8.5, SHRINE_Y + 1.0, SHRINE_Z + 8.5, 180f, 0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 4, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 0, false, false));

        // 3. Celestial audio & visuals
        world.playSound(null, SHRINE_X + 8.5, SHRINE_Y + 1.0, SHRINE_Z + 8.5, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT, 1.8f, 0.9f);
        world.playSound(null, SHRINE_X + 8.5, SHRINE_Y + 1.0, SHRINE_Z + 8.5, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.3f);
        world.spawnParticles(ParticleTypes.END_ROD, SHRINE_X + 8.5, SHRINE_Y + 2.0, SHRINE_Z + 8.5, 80, 2.0, 1.5, 2.0, 0.05);
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, SHRINE_X + 8.5, SHRINE_Y + 2.0, SHRINE_Z + 8.5, 50, 1.5, 1.0, 1.5, 0.1);

        // 4. Divine Title & Dialogue
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 60, 20));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§e§l✦ THE DIVINE AWAKENING ✦")));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("§bSummoned from another world...")));

        player.sendMessage(Text.literal("§e═══════════════════════════════════════════════════"), false);
        player.sendMessage(Text.literal("§6§l[Goddess of Rebirth] §f\"Awaken, wandering soul... You have been chosen to reincarnate into the realm of §b§lAscend§f.\""), false);
        player.sendMessage(Text.literal("§6§l[Goddess of Rebirth] §f\"By divine decree, you may choose your §eRace§f, your §cCombat Class§f, and two foundational §aArts§f before your descent.\""), false);
        player.sendMessage(Text.literal("§6§l[Goddess of Rebirth] §7(Select your identity below to seal the pact and begin your adventure)"), false);
        player.sendMessage(Text.literal("§e═══════════════════════════════════════════════════"), false);

        // 5. Open Awakening Screen
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
        world.spawnParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 3, 0, 0, 0, 0);
        world.spawnParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 2.0, player.getZ(), 40, 1.0, 1.0, 1.0, 0.1);

        // 3. Find safe surface landing position
        int targetX = SHRINE_X + 16;
        int targetZ = SHRINE_Z + 16;
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
                player.getInventory().offerOrDrop(new ItemStack(Items.SHIELD));
                player.getInventory().offerOrDrop(new ItemStack(Items.IRON_AXE));
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
        for (int dx = 0; dx <= 16; dx++) {
            for (int dz = 0; dz <= 16; dz++) {
                double dist = Math.hypot(dx - 8, dz - 8);
                if (dist <= 7.5) {
                    pos.set(SHRINE_X + dx, SHRINE_Y, SHRINE_Z + dz);
                    if (Math.abs(dist - 5.0) < 0.8) {
                        world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
                    } else if (Math.abs(dist - 7.0) < 0.6) {
                        world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
                    } else {
                        world.setBlockState(pos, Blocks.SMOOTH_QUARTZ.getDefaultState());
                    }
                }
            }
        }

        // Center Altar
        pos.set(SHRINE_X + 8, SHRINE_Y + 1, SHRINE_Z + 8);
        world.setBlockState(pos, Blocks.AMETHYST_BLOCK.getDefaultState());
        pos.set(SHRINE_X + 8, SHRINE_Y + 2, SHRINE_Z + 8);
        world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());

        // 4 Celestial Pillars
        int[][] pillars = {{4, 4}, {12, 4}, {4, 12}, {12, 12}};
        for (int[] p : pillars) {
            for (int dy = 1; dy <= 6; dy++) {
                pos.set(SHRINE_X + p[0], SHRINE_Y + dy, SHRINE_Z + p[1]);
                world.setBlockState(pos, Blocks.QUARTZ_PILLAR.getDefaultState());
            }
            pos.set(SHRINE_X + p[0], SHRINE_Y + 7, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
            pos.set(SHRINE_X + p[0], SHRINE_Y + 8, SHRINE_Z + p[1]);
            world.setBlockState(pos, Blocks.END_ROD.getDefaultState());
        }
    }
}

