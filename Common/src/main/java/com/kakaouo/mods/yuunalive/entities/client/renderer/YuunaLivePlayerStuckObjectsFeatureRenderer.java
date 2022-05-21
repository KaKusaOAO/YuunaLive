package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.Random;

public abstract class YuunaLivePlayerStuckObjectsFeatureRenderer<M extends YuunaLivePlayerEntityModel>
        extends RenderLayer<YuunaLivePlayerEntity, M> {
    public YuunaLivePlayerStuckObjectsFeatureRenderer(LivingEntityRenderer<YuunaLivePlayerEntity, M> entityRenderer) {
        super(entityRenderer);
    }

    protected abstract int getObjectCount(YuunaLivePlayerEntity var1);

    protected abstract void renderObject(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, YuunaLivePlayerEntity livingEntity, float f, float g, float h, float j, float k, float l) {
        int m = this.getObjectCount(livingEntity);
        Random random = new Random(livingEntity.getId());
        if (m <= 0) {
            return;
        }
        for (int n = 0; n < m; ++n) {
            matrixStack.pushPose();
            ModelPart modelPart = this.getParentModel().getRandomPart(random);
            ModelPart.Cube cuboid = modelPart.getRandomCube(random);
            modelPart.translateAndRotate(matrixStack);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = Mth.lerp(o, cuboid.minX, cuboid.maxX) / 16.0f;
            float s = Mth.lerp(p, cuboid.minY, cuboid.maxY) / 16.0f;
            float t = Mth.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0f;
            matrixStack.translate(r, s, t);
            o = -1.0f * (o * 2.0f - 1.0f);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            this.renderObject(matrixStack, vertexConsumerProvider, i, livingEntity, o, p, q, h);
            matrixStack.popPose();
        }
    }
}