package net.ascend.mod.item.staff;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MagicStaffItem extends Item {
    public enum StaffType {
        APPRENTICE,
        FLAME,
        STORM,
        SUMMONER,
        VOID
    }

    private final StaffType type;

    public MagicStaffItem(StaffType type, Settings settings) {
        super(settings.maxCount(1));
        this.type = type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.success(stack);

        ServerPlayerEntity player = (ServerPlayerEntity) user;
        ServerWorld serverWorld = (ServerWorld) world;
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getPos();

        switch (type) {
            case APPRENTICE -> {
                if (!data.consumeMana(15.0)) return TypedActionResult.fail(stack);
                SmallFireballEntity bolt = new SmallFireballEntity(world, player, look.x * 2.0, look.y * 2.0, look.z * 2.0);
                bolt.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                world.spawnEntity(bolt);
                serverWorld.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.4f);
                user.getItemCooldownManager().set(this, 15);
            }
            case FLAME -> {
                if (!data.consumeMana(25.0)) return TypedActionResult.fail(stack);
                FireballEntity fb = new FireballEntity(world, player, look.x * 1.5, look.y * 1.5, look.z * 1.5, 1);
                fb.setPosition(pos.x, player.getEyeY() - 0.1, pos.z);
                world.spawnEntity(fb);
                serverWorld.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                user.getItemCooldownManager().set(this, 25);
            }
            case STORM -> {
                if (!data.consumeMana(35.0)) return TypedActionResult.fail(stack);
                HitResult ray = player.raycast(30.0, 0, false);
                Vec3d targetPos = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(15));
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                if (bolt != null) {
                    bolt.refreshPositionAfterTeleport(targetPos);
                    world.spawnEntity(bolt);
                }
                user.getItemCooldownManager().set(this, 30);
            }
            case SUMMONER -> {
                if (!data.consumeMana(40.0)) return TypedActionResult.fail(stack);
                // Grants buff to user and summons a flame minion burst
                serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + 1, pos.z, 40, 1.0, 1.0, 1.0, 0.1);
                serverWorld.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.8f, 1.5f);
                user.getItemCooldownManager().set(this, 60);
            }
            case VOID -> {
                if (!data.consumeMana(60.0)) return TypedActionResult.fail(stack);
                // Gravity pull + dark shockwave
                HitResult ray = player.raycast(25.0, 0, false);
                Vec3d center = ray.getType() != HitResult.Type.MISS ? ray.getPos() : pos.add(look.multiply(10));
                serverWorld.spawnParticles(ParticleTypes.PORTAL, center.x, center.y + 1, center.z, 80, 3.0, 3.0, 3.0, 0.5);

                Box pullBox = new Box(center.x - 8, center.y - 4, center.z - 8, center.x + 8, center.y + 6, center.z + 8);
                List<LivingEntity> enemies = serverWorld.getEntitiesByClass(LivingEntity.class, pullBox, e -> e != player && e.isAlive());
                for (LivingEntity e : enemies) {
                    Vec3d toCenter = center.subtract(e.getPos()).normalize().multiply(1.5);
                    e.setVelocity(toCenter.x, 0.4, toCenter.z);
                    e.damage(player.getDamageSources().magic(), 14.0f + (data.getStrength() * 0.8f));
                }
                serverWorld.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.5f, 0.8f);
                user.getItemCooldownManager().set(this, 100);
            }
        }
        return TypedActionResult.success(stack);
    }
}

