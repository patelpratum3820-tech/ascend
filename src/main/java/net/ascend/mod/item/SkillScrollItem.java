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

public class SkillScrollItem extends Item {
    private final SkillRank rank;

    public SkillScrollItem(Settings settings, SkillRank rank) {
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

            // Find a skill of this rank that is not yet unlocked
            List<SkillInfo> available = SkillInfo.getSkillsByRank(rank);
            SkillInfo targetSkill = null;
            for (SkillInfo s : available) {
                if (!data.hasUnlockedSkill(s.id())) {
                    targetSkill = s;
                    break;
                }
            }

            if (targetSkill == null) {
                player.sendMessage(Text.literal("§eYou have already mastered all known " + rank.getFormatted() + "§e rank skills!"), true);
                return TypedActionResult.pass(stack);
            }

            // Unlock skill
            data.unlockSkill(targetSkill.id());
            AscendPackets.syncPlayerData(player);

            player.sendMessage(Text.literal("§6§l[SKILL UNLOCKED] §fYou mastered " + rank.getFormatted() + " §f" + targetSkill.displayName() + "!"), false);
            player.sendMessage(Text.literal("§7Tag: " + targetSkill.tag() + " | " + targetSkill.description()), false);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.2f);

            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("§7Tier: " + rank.getFormatted() + " Rank"));
        tooltip.add(Text.literal("§eRight-click to comprehend and unlock a skill."));
        tooltip.add(Text.literal("§8Dropped by dungeon bosses and elite monsters."));
    }
}

