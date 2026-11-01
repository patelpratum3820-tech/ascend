package net.ascend.mod.item;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.ascend.mod.skills.SkillInfo;
import net.ascend.mod.skills.SkillRank;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkillTomeItem extends Item {
    private final SkillRank rank;

    public SkillTomeItem(Settings settings, SkillRank rank) {
        super(settings);
        this.rank = rank;
    }

    public SkillRank getRank() {
        return rank;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();

            // Check level requirement for higher ranks
            int minLevel = switch (rank) {
                case B -> 35;
                case A -> 50;
                case S -> 70;
                case SS -> 100;
                default -> 1;
            };

            if (data.getLevel() < minLevel) {
                player.sendMessage(Text.literal("§cYou must reach Level " + minLevel + " to comprehend this " + rank.getFormatted() + "§c Tome!"), true);
                return TypedActionResult.fail(stack);
            }

            AscendPackets.sendOpenSkillUnlock(player, rank.name(), hand == Hand.MAIN_HAND);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("§7Ancient Grimoire: " + rank.getFormatted() + " Rank"));
        int minLevel = switch (rank) {
            case B -> 35;
            case A -> 50;
            case S -> 70;
            case SS -> 100;
            default -> 1;
        };
        tooltip.add(Text.literal("§6✦ Required Level: §f" + minLevel));
        tooltip.add(Text.literal("§eRight-click to absorb ancient technique."));
    }
}

