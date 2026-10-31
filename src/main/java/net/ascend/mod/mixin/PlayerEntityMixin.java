package net.ascend.mod.mixin;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.minecraft.entity.damage.DamageSource;
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
    private float ascend$applyDefenseReduction(float amount, DamageSource source) {
        if (!source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_ARMOR)) {
            amount *= (float) ascend$getPlayerData().getDamageReductionMultiplier();
        }
        return amount;
    }
}
