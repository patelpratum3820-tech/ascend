package net.ascend.mod.entity.mob;

import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class MoonMothEntity extends BatEntity {

    public MoonMothEntity(EntityType<? extends BatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.random.nextInt(3) == 0) {
            this.getWorld().addParticle(
                    ParticleTypes.END_ROD,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.4,
                    this.getY() + this.random.nextDouble() * 0.4,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.4,
                    0.0, 0.01, 0.0
            );
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        this.dropStack(new ItemStack(AscendItems.MANA_CRYSTAL, 1));
    }
}
