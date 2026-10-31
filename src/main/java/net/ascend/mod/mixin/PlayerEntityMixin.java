package net.ascend.mod.mixin;

import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
    private float ascend$applyCombatAndDefense(float amount, DamageSource source) {
        PlayerData data = ascend$getPlayerData();
        AscendConfig cfg = AscendConfig.INSTANCE;

        // 1. Invulnerability frames (Dash i-frames)
        if (data.getInvulnerableTicks() > 0) {
            return 0.0f;
        }

        // 2. Dragonoid Perks & Weaknesses
        if ("Dragonoid".equalsIgnoreCase(data.getRace())) {
            if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || 
                source.isOf(DamageTypes.LAVA) || source.isOf(DamageTypes.HOT_FLOOR)) {
                return 0.0f; // Fire immunity
            }
            if (source.isOf(DamageTypes.FREEZE) || source.isOf(DamageTypes.DROWN)) {
                amount *= (float) cfg.dragonoidColdWaterVulnerability; // +50% extra cold/water damage
            }
        }

        // 3. Slime Fall Damage (Balanced: 75% reduction instead of infinite immunity)
        if ("Slime".equalsIgnoreCase(data.getRace())) {
            if (source.isOf(DamageTypes.FALL)) {
                amount *= 0.25f; // 75% fall damage reduction
            }
        }

        // 4. Armor Set Bonus (Dragonite full set fire immunity)
        if (ascend$hasArmorSet("dragonite_")) {
            if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.LAVA)) {
                return 0.0f;
            }
        }

        // 5. Defense reduction
        if (!source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_ARMOR)) {
            amount *= (float) data.getDamageReductionMultiplier();
        }

        return amount;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ascend$tickCombatState(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        PlayerData data = ascend$getPlayerData();
        data.tickResources();

        if (!self.getWorld().isClient() && self.isAlive()) {
            // Slime passive regeneration (capped)
            if ("Slime".equalsIgnoreCase(data.getRace())) {
                if (self.getHealth() < self.getMaxHealth() && self.getRandom().nextFloat() < AscendConfig.INSTANCE.slimeRegenChancePerTick) {
                    self.heal(1.0f);
                }
            }

            // Mythril 4-piece set bonus: bonus mana regen
            if (ascend$hasArmorSet("mythril_")) {
                data.setCurrentMana(Math.min(data.getMana(), data.getCurrentMana() + 0.1));
            }

            // Sync resources periodically to client (every 10 ticks = 0.5s)
            if (self.age % 10 == 0 && self instanceof ServerPlayerEntity serverPlayer) {
                AscendPackets.syncPlayerData(serverPlayer);
            }
        }
    }

    @Unique
    private boolean ascend$hasArmorSet(String prefix) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        return ascend$isPiece(self.getEquippedStack(EquipmentSlot.HEAD), prefix) &&
               ascend$isPiece(self.getEquippedStack(EquipmentSlot.CHEST), prefix) &&
               ascend$isPiece(self.getEquippedStack(EquipmentSlot.LEGS), prefix) &&
               ascend$isPiece(self.getEquippedStack(EquipmentSlot.FEET), prefix);
    }

    @Unique
    private boolean ascend$isPiece(ItemStack stack, String prefix) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id.getNamespace().equals("ascend") && id.getPath().startsWith(prefix);
    }
}
