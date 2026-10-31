package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class DungeonGolemEntity extends IronGolemEntity {

    public DungeonGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        int ingots = 1 + this.random.nextInt(2 + lootingMultiplier);
        this.dropStack(new ItemStack(AscendItems.MYTHRIL_INGOT, ingots));
        this.dropStack(new ItemStack(Items.COBBLESTONE, 3 + this.random.nextInt(6)));
        if (this.random.nextFloat() < 0.35f + (0.1f * lootingMultiplier)) {
            this.dropStack(new ItemStack(AscendItems.DUNGEON_MAP, 1));
        }
    }
}
