package net.ascend.mod.entity;

import net.ascend.mod.AscendMod;
import net.ascend.mod.entity.boss.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AscendEntities {
    public static final EntityType<GoblinKingEntity> GOBLIN_KING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "goblin_king"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GoblinKingEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 2.2f))
                    .build()
    );

    public static final EntityType<ShadowKnightEntity> SHADOW_KNIGHT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "shadow_knight"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShadowKnightEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.4f))
                    .build()
    );

    public static final EntityType<CrystalGolemEntity> CRYSTAL_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "crystal_golem"),
            FabricEntityTypeBuilder.<CrystalGolemEntity>create(SpawnGroup.MONSTER, CrystalGolemEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f, 2.7f))
                    .build()
    );

    public static final EntityType<VoidLichEntity> VOID_LICH = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "void_lich"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, VoidLichEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 2.5f))
                    .build()
    );

    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(GOBLIN_KING, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0));

        FabricDefaultAttributeRegistry.register(SHADOW_KNIGHT, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 350.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 18.0)
                .add(EntityAttributes.GENERIC_ARMOR, 14.0));

        FabricDefaultAttributeRegistry.register(CRYSTAL_GOLEM, net.minecraft.entity.passive.IronGolemEntity.createIronGolemAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 24.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
                .add(EntityAttributes.GENERIC_ARMOR, 20.0));

        FabricDefaultAttributeRegistry.register(VOID_LICH, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 650.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 28.0)
                .add(EntityAttributes.GENERIC_ARMOR, 16.0));
    }
}
