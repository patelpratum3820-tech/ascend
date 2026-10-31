package net.ascend.mod.item;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.dungeon.DungeonManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DungeonMapItem extends Item {
    public DungeonMapItem(Settings settings) {
        super(settings.maxCount(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.success(stack);

        ServerPlayerEntity player = (ServerPlayerEntity) user;
        ServerWorld serverWorld = (ServerWorld) world;
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();

        // Determine Dungeon rank based on player rank
        DungeonManager.DungeonRank rank = switch (data.getRank()) {
            case "S" -> DungeonManager.DungeonRank.S;
            case "A" -> DungeonManager.DungeonRank.A;
            case "B" -> DungeonManager.DungeonRank.B;
            case "C" -> DungeonManager.DungeonRank.C;
            case "D" -> DungeonManager.DungeonRank.D;
            default -> DungeonManager.DungeonRank.E;
        };

        DungeonManager.generateDungeonAt(serverWorld, player.getBlockPos().add(30, 0, 30), rank, player);
        serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (!player.isCreative()) {
            stack.decrement(1);
        }
        return TypedActionResult.consume(stack);
    }
}

