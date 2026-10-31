package net.ascend.mod.mixin;

import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerDataAccessor {
    @Unique
    private PlayerData ascend$playerData = new PlayerData();

    @Override
    public PlayerData ascend$getPlayerData() {
        if (this.ascend$playerData == null) {
            this.ascend$playerData = new PlayerData();
        }
        return this.ascend$playerData;
    }

    @Override
    public void ascend$setPlayerData(PlayerData data) {
        this.ascend$playerData = data;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void ascend$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound ascendTag = new NbtCompound();
        ascend$getPlayerData().writeToNbt(ascendTag);
        nbt.put("AscendRPG", ascendTag);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void ascend$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AscendRPG")) {
            ascend$getPlayerData().readFromNbt(nbt.getCompound("AscendRPG"));
        }
        PlayerEntity self = (PlayerEntity) (Object) this;
        ascend$getPlayerData().applyModifiers(self);
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float ascend$applyPerksAndDefense(float amount, DamageSource source) {
        PlayerData data = ascend$getPlayerData();
        AscendConfig cfg = AscendConfig.INSTANCE;

        // 1. Dragonoid Perks & Weaknesses
        if ("Dragonoid".equalsIgnoreCase(data.getRace())) {
            if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || 
                source.isOf(DamageTypes.LAVA) || source.isOf(DamageTypes.HOT_FLOOR)) {
                return 0.0f; // Fire immunity
            }
            if (source.isOf(DamageTypes.FREEZE) || source.isOf(DamageTypes.DROWN)) {
                amount *= (float) cfg.dragonoidColdWaterVulnerability; // +50% extra cold/water damage
            }
        }

        // 2. Slime Fall Immunity
        if ("Slime".equalsIgnoreCase(data.getRace())) {
            if (source.isOf(DamageTypes.FALL)) {
                return 0.0f; // No fall damage
            }
        }

        // 3. Defense reduction
        if (!source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_ARMOR)) {
            amount *= (float) data.getDamageReductionMultiplier();
        }

        return amount;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ascend$tickPassiveRegen(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!self.getWorld().isClient() && self.isAlive()) {
            PlayerData data = ascend$getPlayerData();
            // Slime passive regeneration
            if ("Slime".equalsIgnoreCase(data.getRace())) {
                if (self.getHealth() < self.getMaxHealth() && self.getRandom().nextFloat() < AscendConfig.INSTANCE.slimeRegenChancePerTick) {
                    self.heal(1.0f);
                }
            }
        }
    }
}
