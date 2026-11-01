package net.ascend.mod.client.render.mob;

import net.ascend.mod.AscendMod;
import net.ascend.mod.entity.mob.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AscendMobRenderers {

    // 1. Spirit Fox
    public static class SpiritFoxRenderer extends FoxEntityRenderer {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/spirit_fox.png");

        public SpiritFoxRenderer(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(FoxEntity entity) {
            return TEXTURE;
        }
    }

    // 2. Mana Deer
    public static class ManaDeerRenderer extends MobEntityRenderer<ManaDeerEntity, CowEntityModel<ManaDeerEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/mana_deer.png");

        public ManaDeerRenderer(EntityRendererFactory.Context context) {
            super(context, new CowEntityModel<>(context.getPart(EntityModelLayers.COW)), 0.7f);
        }

        @Override
        public Identifier getTexture(ManaDeerEntity entity) {
            return TEXTURE;
        }
    }

    // 3. Slime Pup
    public static class SlimePupRenderer extends MobEntityRenderer<SlimePupEntity, SlimeEntityModel<SlimePupEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/slime_pup.png");

        public SlimePupRenderer(EntityRendererFactory.Context context) {
            super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.3f);
        }

        @Override
        public Identifier getTexture(SlimePupEntity entity) {
            return TEXTURE;
        }
    }

    // 4. Moon Moth
    public static class MoonMothRenderer extends BatEntityRenderer {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/moon_moth.png");

        public MoonMothRenderer(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(BatEntity entity) {
            return TEXTURE;
        }
    }

    // 5. Goblin
    public static class GoblinRenderer extends BipedEntityRenderer<GoblinEntity, BipedEntityModel<GoblinEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/goblin.png");

        public GoblinRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
        }

        @Override
        public Identifier getTexture(GoblinEntity entity) {
            return TEXTURE;
        }
    }

    // 6. Orc Warrior
    public static class OrcWarriorRenderer extends BipedEntityRenderer<OrcWarriorEntity, BipedEntityModel<OrcWarriorEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/orc_warrior.png");

        public OrcWarriorRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.6f);
        }

        @Override
        public Identifier getTexture(OrcWarriorEntity entity) {
            return TEXTURE;
        }
    }

    // 7. Shadow Wraith
    public static class ShadowWraithRenderer extends BipedEntityRenderer<ShadowWraithEntity, BipedEntityModel<ShadowWraithEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/shadow_wraith.png");

        public ShadowWraithRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.5f);
        }

        @Override
        public Identifier getTexture(ShadowWraithEntity entity) {
            return TEXTURE;
        }
    }

    // 8. Skeleton Knight
    public static class SkeletonKnightRenderer extends BipedEntityRenderer<SkeletonKnightEntity, BipedEntityModel<SkeletonKnightEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/skeleton_knight.png");

        public SkeletonKnightRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.55f);
        }

        @Override
        public Identifier getTexture(SkeletonKnightEntity entity) {
            return TEXTURE;
        }
    }

    // 9. Dark Mage
    public static class DarkMageRenderer extends BipedEntityRenderer<DarkMageEntity, BipedEntityModel<DarkMageEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/dark_mage.png");

        public DarkMageRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.5f);
        }

        @Override
        public Identifier getTexture(DarkMageEntity entity) {
            return TEXTURE;
        }
    }

    // 10. Fire Drake
    public static class FireDrakeRenderer extends PhantomEntityRenderer {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/fire_drake.png");

        public FireDrakeRenderer(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(PhantomEntity entity) {
            return TEXTURE;
        }
    }

    // 11. Dungeon Golem
    public static class DungeonGolemRenderer extends IronGolemEntityRenderer {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/mob/dungeon_golem.png");

        public DungeonGolemRenderer(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(IronGolemEntity entity) {
            return TEXTURE;
        }
    }

    // Step R2 Ranked Zombie & Skeleton Renderers
    public static class AscendZombieRenderer extends ZombieEntityRenderer {
        public AscendZombieRenderer(EntityRendererFactory.Context context) {
            super(context);
        }
    }

    public static class AscendSkeletonRenderer extends SkeletonEntityRenderer {
        public AscendSkeletonRenderer(EntityRendererFactory.Context context) {
            super(context);
        }
    }
}
