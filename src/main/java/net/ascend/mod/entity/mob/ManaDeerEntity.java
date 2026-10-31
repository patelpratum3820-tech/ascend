package net.ascend.mod.entity.mob;

import net.ascend.mod.block.AscendBlocks;
import net.ascend.mod.item.AscendItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ManaDeerEntity extends AnimalEntity {

    public ManaDeerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.3));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0f, 1.1, 1.3));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.ofItems(AscendBlocks.MYSTIC_LEAVES.asItem(), Items.WHEAT), false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean causedByPlayer) {
        super.dropEquipment(source, lootingMultiplier, causedByPlayer);
        // Drops Mystic Log / Leather, and rare Mana Crystal
        int logs = 1 + this.random.nextInt(3 + lootingMultiplier);
        this.dropStack(new ItemStack(AscendBlocks.MYSTIC_LOG.asItem(), logs));
        this.dropStack(new ItemStack(Items.LEATHER, 1 + this.random.nextInt(2)));
        if (this.random.nextFloat() < 0.25f + (0.05f * lootingMultiplier)) {
            this.dropStack(new ItemStack(AscendItems.MANA_CRYSTAL, 1));
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(AscendBlocks.MYSTIC_LEAVES.asItem()) || stack.isOf(Items.WHEAT);
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return (PassiveEntity) this.getType().create(world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COW_DEATH;
    }
}
