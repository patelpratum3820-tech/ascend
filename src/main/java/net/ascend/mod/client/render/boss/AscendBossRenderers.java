package net.ascend.mod.client.render.boss;

import net.ascend.mod.AscendMod;
import net.ascend.mod.entity.boss.CrystalGolemEntity;
import net.ascend.mod.entity.boss.GoblinKingEntity;
import net.ascend.mod.entity.boss.ShadowKnightEntity;
import net.ascend.mod.entity.boss.VoidLichEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AscendBossRenderers {

    public static class GoblinKingRenderer extends BipedEntityRenderer<GoblinKingEntity, BipedEntityModel<GoblinKingEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/boss/goblin_king.png");

        public GoblinKingRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.6f);
        }

        @Override
        public Identifier getTexture(GoblinKingEntity entity) {
            return TEXTURE;
        }
    }

    public static class ShadowKnightRenderer extends BipedEntityRenderer<ShadowKnightEntity, BipedEntityModel<ShadowKnightEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/boss/shadow_knight.png");

        public ShadowKnightRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.SKELETON)), 0.6f);
        }

        @Override
        public Identifier getTexture(ShadowKnightEntity entity) {
            return TEXTURE;
        }
    }

    public static class CrystalGolemRenderer extends IronGolemEntityRenderer {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/boss/crystal_golem.png");

        public CrystalGolemRenderer(EntityRendererFactory.Context context) {
            super(context);
        }

        @Override
        public Identifier getTexture(net.minecraft.entity.passive.IronGolemEntity entity) {
            return TEXTURE;
        }
    }

    public static class VoidLichRenderer extends BipedEntityRenderer<VoidLichEntity, BipedEntityModel<VoidLichEntity>> {
        private static final Identifier TEXTURE = new Identifier(AscendMod.MOD_ID, "textures/entity/boss/void_lich.png");

        public VoidLichRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.WITHER_SKELETON)), 0.6f);
        }

        @Override
        public Identifier getTexture(VoidLichEntity entity) {
            return TEXTURE;
        }
    }
}
