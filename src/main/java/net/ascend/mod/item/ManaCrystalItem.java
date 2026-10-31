package net.ascend.mod.item;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ManaCrystalItem extends Item {
    public ManaCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        PlayerData data = ((IPlayerDataAccessor) user).ascend$getPlayerData();

        if (!data.isInitialized()) {
            return TypedActionResult.pass(stack);
        }

        if (data.getCurrentMana() >= data.getMana()) {
            if (!world.isClient) {
                user.sendMessage(Text.literal("§b✦ Mana is already full!"), true);
            }
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {
            data.setCurrentMana(Math.min(data.getMana(), data.getCurrentMana() + 50.0));
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            if (user instanceof ServerPlayerEntity serverPlayer) {
                AscendPackets.syncPlayerData(serverPlayer);
                serverPlayer.getServerWorld().spawnParticles(
                        ParticleTypes.ENCHANT,
                        user.getX(), user.getY() + 1.0, user.getZ(),
                        25, 0.4, 0.5, 0.4, 0.2
                );
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.2f, 1.5f);
            user.sendMessage(Text.literal("§b✦ Mana Crystal Consumed! §e(+50 MP)"), true);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}

