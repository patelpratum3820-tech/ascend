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
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.Optional;

public class DivineAwakening {

    public static final int SHRINE_X = 0;
    public static final int SHRINE_Y = 220;
    public static final int SHRINE_Z = 0;

    public static void initiateAwakening(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // 1. Place the full grand Temple of the Chosen NBT structure matching reference art
        buildCelestialPlatform(world);

        // 2. Safe positioning on the Temple Courtyard facing North towards the Goddess Statue & Sanctuary
        player.teleport(world, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 6.5, 0f, 0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 4, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2400, 0, false, false));

        // 3. Celestial audio & visuals
        world.playSound(null, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 0.5, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT, 2.0f, 0.9f);
        world.playSound(null, SHRINE_X + 0.5, SHRINE_Y + 1.0, SHRINE_Z + 0.5, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.2f, 1.4f);
        world.spawnParticles(ParticleTypes.END_ROD, SHRINE_X + 0.5, SHRINE_Y + 2.0, SHRINE_Z + 0.5, 100, 4.0, 2.5, 4.0, 0.05);
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, SHRINE_X + 0.5, SHRINE_Y + 2.0, SHRINE_Z + 0.5, 80, 3.0, 2.0, 3.0, 0.1);

        // 4. Divine Title
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 50, 20));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("§e§l✦ TEMPLE OF THE CHOSEN ✦")));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("§bA Goddess summons you from across worlds...")));

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
        giveStarterGear(player, data.getPrimaryClass(), data.getSecondaryClass());

        // Surface arrival particles
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, targetX + 0.5, topY + 1.5, targetZ + 0.5, 60, 1.0, 1.5, 1.0, 0.1);
        world.playSound(null, targetX + 0.5, topY + 1.0, targetZ + 0.5, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.2f);
    }

    private static void giveStarterGear(ServerPlayerEntity player, String pClass, String sClass) {
        player.getInventory().offerOrDrop(new ItemStack(Items.BREAD, 16));
        grantClassItems(player, pClass);
        if (sClass != null && !sClass.equalsIgnoreCase(pClass)) {
            grantClassItems(player, sClass);
        }
    }

    private static void grantClassItems(ServerPlayerEntity player, String pClass) {
        switch (pClass.toLowerCase()) {
            case "swordsman" -> player.getInventory().offerOrDrop(new ItemStack(Items.IRON_SWORD));
            case "archer" -> {
                player.getInventory().offerOrDrop(new ItemStack(Items.BOW));
                player.getInventory().offerOrDrop(new ItemStack(Items.ARROW, 64));
            }
            case "tank" -> {
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
        StructureTemplateManager manager = world.getStructureTemplateManager();
        StructureTemplate template = null;
        Optional<StructureTemplate> templateOpt = manager.getTemplate(new Identifier("ascend", "temple_of_the_chosen"));
        if (templateOpt.isPresent()) {
            template = templateOpt.get();
        } else {
            // Fallback direct classpath load
            try (java.io.InputStream is = DivineAwakening.class.getResourceAsStream("/data/ascend/structures/temple_of_the_chosen.nbt")) {
                if (is != null) {
                    net.minecraft.nbt.NbtCompound nbt = net.minecraft.nbt.NbtIo.readCompressed(is);
                    template = manager.createTemplate(nbt);
                }
            } catch (Exception e) {
                net.ascend.mod.AscendMod.LOGGER.error("[Ascend] Error loading temple_of_the_chosen NBT directly", e);
            }
        }

        if (template != null) {
            // Clear old blocks from legacy test platforms around spawn
            for (int x = -20; x <= 20; x++) {
                for (int z = -20; z <= 20; z++) {
                    for (int y = SHRINE_Y - 12; y <= SHRINE_Y + 25; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (!world.getBlockState(p).isAir()) {
                            world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                        }
                    }
                }
            }

            BlockPos origin = new BlockPos(
                    SHRINE_X - template.getSize().getX() / 2,
                    SHRINE_Y - 10,
                    SHRINE_Z - template.getSize().getZ() / 2
            );
            StructurePlacementData placementData = new StructurePlacementData();
            template.place(world, origin, origin, placementData, world.getRandom(), 2);
            net.ascend.mod.AscendMod.LOGGER.info("[Ascend] Successfully placed Temple of the Chosen NBT structure at " + origin);
        } else {
            net.ascend.mod.AscendMod.LOGGER.error("[Ascend] Failed to load temple_of_the_chosen.nbt!");
        }
    }
}
