package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpiritFoxEntity extends FoxEntity {

    public SpiritFoxEntity(EntityType<? extends FoxEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        int crystals = 1 + this.random.nextInt(1 + lootingMultiplier);
        this.dropStack(new ItemStack(AscendItems.MANA_CRYSTAL, crystals));
    }
}
