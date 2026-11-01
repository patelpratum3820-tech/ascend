package net.ascend.mod.entity;

import net.ascend.mod.AscendMod;
import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.entity.boss.*;
import net.ascend.mod.entity.mob.*;
import net.ascend.mod.entity.mob.zombie.*;
import net.ascend.mod.entity.mob.skeleton.*;
import net.ascend.mod.world.AscendBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class AscendEntities {

    // --- Boss Entities ---
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

    // --- Step W5 Mob Entities ---
    public static final EntityType<SpiritFoxEntity> SPIRIT_FOX = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "spirit_fox"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SpiritFoxEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.7f))
                    .build()
    );

    public static final EntityType<ManaDeerEntity> MANA_DEER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "mana_deer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ManaDeerEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 1.4f))
                    .build()
    );

    public static final EntityType<SlimePupEntity> SLIME_PUP = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "slime_pup"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SlimePupEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.6f))
                    .build()
    );

    public static final EntityType<MoonMothEntity> MOON_MOTH = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "moon_moth"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, MoonMothEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.4f))
                    .build()
    );

    public static final EntityType<GoblinEntity> GOBLIN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "goblin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GoblinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.5f))
                    .build()
    );

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.1f))
                    .build()
    );

    public static final EntityType<ShadowWraithEntity> SHADOW_WRAITH = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "shadow_wraith"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShadowWraithEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.9f))
                    .build()
    );

    public static final EntityType<SkeletonKnightEntity> SKELETON_KNIGHT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_knight"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonKnightEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 2.0f))
                    .build()
    );

    public static final EntityType<DarkMageEntity> DARK_MAGE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "dark_mage"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DarkMageEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<FireDrakeEntity> FIRE_DRAKE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "fire_drake"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FireDrakeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.8f))
                    .build()
    );

    public static final EntityType<DungeonGolemEntity> DUNGEON_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "dungeon_golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DungeonGolemEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f, 2.7f))
                    .build()
    );

    // --- Step R2 Zombie Family ---
    public static final EntityType<ZombieSoldierEntity> ZOMBIE_SOLDIER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_soldier"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieSoldierEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<ZombieBruteEntity> ZOMBIE_BRUTE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_brute"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieBruteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 2.3f))
                    .build()
    );

    public static final EntityType<ZombieBerserkerEntity> ZOMBIE_BERSERKER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_berserker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieBerserkerEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<ZombieSpearmanEntity> ZOMBIE_SPEARMAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_spearman"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieSpearmanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<PlagueZombieEntity> PLAGUE_ZOMBIE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "plague_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, PlagueZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<ZombieNecromancerEntity> ZOMBIE_NECROMANCER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_necromancer"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieNecromancerEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<ZombieLordEntity> ZOMBIE_LORD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "zombie_lord"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieLordEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.4f))
                    .build()
    );

    // --- Step R2 Skeleton Family ---
    public static final EntityType<SkeletonArcherEntity> SKELETON_ARCHER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_archer"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonArcherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f))
                    .build()
    );

    public static final EntityType<SkeletonCrossbowmanEntity> SKELETON_CROSSBOWMAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_crossbowman"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonCrossbowmanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f))
                    .build()
    );

    public static final EntityType<SkeletonSwordsmanEntity> SKELETON_SWORDSMAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_swordsman"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonSwordsmanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f))
                    .build()
    );

    public static final EntityType<BoneKnightEntity> BONE_KNIGHT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "bone_knight"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BoneKnightEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 2.1f))
                    .build()
    );

    public static final EntityType<SkeletonMageEntity> SKELETON_MAGE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_mage"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonMageEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f))
                    .build()
    );

    public static final EntityType<BoneReaperEntity> BONE_REAPER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "bone_reaper"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BoneReaperEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 2.2f))
                    .build()
    );

    public static final EntityType<SkeletonCommanderEntity> SKELETON_COMMANDER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AscendMod.MOD_ID, "skeleton_commander"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonCommanderEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.4f))
                    .build()
    );

    public static void registerEntities() {
        // Boss Attributes
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

        // Step W5 Mob Attributes
        FabricDefaultAttributeRegistry.register(SPIRIT_FOX, net.minecraft.entity.passive.FoxEntity.createFoxAttributes());
        FabricDefaultAttributeRegistry.register(MANA_DEER, ManaDeerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SLIME_PUP, SlimePupEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MOON_MOTH, net.minecraft.entity.passive.BatEntity.createBatAttributes());
        FabricDefaultAttributeRegistry.register(GOBLIN, GoblinEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ORC_WARRIOR, OrcWarriorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SHADOW_WRAITH, ShadowWraithEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKELETON_KNIGHT, SkeletonKnightEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DARK_MAGE, DarkMageEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FIRE_DRAKE, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0));
        FabricDefaultAttributeRegistry.register(DUNGEON_GOLEM, net.minecraft.entity.passive.IronGolemEntity.createIronGolemAttributes());

        // Step R2 Zombie Attributes
        FabricDefaultAttributeRegistry.register(ZOMBIE_SOLDIER, ZombieSoldierEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZOMBIE_BRUTE, ZombieBruteEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZOMBIE_BERSERKER, ZombieBerserkerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZOMBIE_SPEARMAN, ZombieSpearmanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(PLAGUE_ZOMBIE, PlagueZombieEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZOMBIE_NECROMANCER, ZombieNecromancerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ZOMBIE_LORD, ZombieLordEntity.createAttributes());

        // Step R2 Skeleton Attributes
        FabricDefaultAttributeRegistry.register(SKELETON_ARCHER, SkeletonArcherEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKELETON_CROSSBOWMAN, SkeletonCrossbowmanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKELETON_SWORDSMAN, SkeletonSwordsmanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BONE_KNIGHT, BoneKnightEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKELETON_MAGE, SkeletonMageEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BONE_REAPER, BoneReaperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKELETON_COMMANDER, SkeletonCommanderEntity.createAttributes());

        // Spawn Restrictions
        SpawnRestriction.register(SPIRIT_FOX, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(MANA_DEER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(SLIME_PUP, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(MOON_MOTH, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, (type, world, reason, pos, random) -> pos.getY() >= world.getSeaLevel());
        SpawnRestriction.register(GOBLIN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ORC_WARRIOR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SHADOW_WRAITH, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SKELETON_KNIGHT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(DARK_MAGE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(FIRE_DRAKE, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, HostileEntity::canMobSpawn);
        SpawnRestriction.register(DUNGEON_GOLEM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);

        // Step R2 Zombie Spawn Restrictions
        SpawnRestriction.register(ZOMBIE_SOLDIER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ZOMBIE_BRUTE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ZOMBIE_BERSERKER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ZOMBIE_SPEARMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(PLAGUE_ZOMBIE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ZOMBIE_NECROMANCER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(ZOMBIE_LORD, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);

        // Step R2 Skeleton Spawn Restrictions
        SpawnRestriction.register(SKELETON_ARCHER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SKELETON_CROSSBOWMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SKELETON_SWORDSMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(BONE_KNIGHT, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SKELETON_MAGE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(BONE_REAPER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);
        SpawnRestriction.register(SKELETON_COMMANDER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canMobSpawn);

        registerSpawns();
    }

    private static void registerSpawns() {
        AscendConfig cfg = AscendConfig.INSTANCE;

        // 1. Spirit Fox: Enchanted Forest (passive)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.ENCHANTED_FOREST),
                SpawnGroup.CREATURE,
                SPIRIT_FOX,
                cfg.spiritFoxWeight,
                cfg.spiritFoxMinGroup,
                cfg.spiritFoxMaxGroup
        );

        // 2. Mana Deer: Enchanted Forest (passive)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.ENCHANTED_FOREST),
                SpawnGroup.CREATURE,
                MANA_DEER,
                cfg.manaDeerWeight,
                cfg.manaDeerMinGroup,
                cfg.manaDeerMaxGroup
        );

        // 3. Slime Pup: Shadow Marsh & Crystal Caverns & Caves
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.SHADOW_MARSH, AscendBiomes.CRYSTAL_CAVERNS),
                SpawnGroup.MONSTER,
                SLIME_PUP,
                cfg.slimePupWeight,
                cfg.slimePupMinGroup,
                cfg.slimePupMaxGroup
        );

        // 4. Moon Moth: Enchanted Forest (ambient)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.ENCHANTED_FOREST),
                SpawnGroup.AMBIENT,
                MOON_MOTH,
                cfg.moonMothWeight,
                cfg.moonMothMinGroup,
                cfg.moonMothMaxGroup
        );

        // 5. Goblin: Plains & Forests at night (pack mob)
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_FOREST).or(BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS)),
                SpawnGroup.MONSTER,
                GOBLIN,
                cfg.goblinWeight,
                cfg.goblinMinGroup,
                cfg.goblinMaxGroup
        );

        // 6. Orc Warrior: Emberlands & Plains at night
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.EMBERLANDS, BiomeKeys.PLAINS),
                SpawnGroup.MONSTER,
                ORC_WARRIOR,
                cfg.orcWarriorWeight,
                cfg.orcWarriorMinGroup,
                cfg.orcWarriorMaxGroup
        );

        // 7. Shadow Wraith: Shadow Marsh & dark places
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.SHADOW_MARSH),
                SpawnGroup.MONSTER,
                SHADOW_WRAITH,
                cfg.shadowWraithWeight,
                cfg.shadowWraithMinGroup,
                cfg.shadowWraithMaxGroup
        );

        // 8. Skeleton Knight: Caves & Crystal Caverns
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.CRYSTAL_CAVERNS).or(BiomeSelectors.foundInOverworld()),
                SpawnGroup.MONSTER,
                SKELETON_KNIGHT,
                cfg.skeletonKnightWeight,
                cfg.skeletonKnightMinGroup,
                cfg.skeletonKnightMaxGroup
        );

        // 9. Dark Mage: Shadow Marsh
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.SHADOW_MARSH),
                SpawnGroup.MONSTER,
                DARK_MAGE,
                cfg.darkMageWeight,
                cfg.darkMageMinGroup,
                cfg.darkMageMaxGroup
        );

        // 10. Fire Drake: Emberlands & Dragon Peaks
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(AscendBiomes.EMBERLANDS, AscendBiomes.DRAGON_PEAKS),
                SpawnGroup.MONSTER,
                FIRE_DRAKE,
                cfg.fireDrakeWeight,
                cfg.fireDrakeMinGroup,
                cfg.fireDrakeMaxGroup
        );
    }
}
