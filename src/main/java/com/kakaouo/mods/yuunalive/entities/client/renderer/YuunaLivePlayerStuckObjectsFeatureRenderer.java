package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public abstract class YuunaLivePlayerStuckObjectsFeatureRenderer<M extends YuunaLivePlayerEntityModel>
        extends FeatureRenderer<YuunaLivePlayerEntity, M> {
    public YuunaLivePlayerStuckObjectsFeatureRenderer(LivingEntityRenderer<YuunaLivePlayerEntity, M> entityRenderer) {
        super(entityRenderer);
    }

    protected abstract int getObjectCount(YuunaLivePlayerEntity var1);

    protected abstract void renderObject(MatrixStack var1, VertexConsumerProvider var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, YuunaLivePlayerEntity livingEntity, float f, float g, float h, float j, float k, float l) {
        int m = this.getObjectCount(livingEntity);
        Random random = new Random(livingEntity.getId());
        if (m <= 0) {
            return;
        }
        for (int n = 0; n < m; ++n) {
            matrixStack.push();
            ModelPart modelPart = this.getContextModel().getRandomPart(random);
            ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
            modelPart.rotate(matrixStack);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = MathHelper.lerp(o, cuboid.minX, cuboid.maxX) / 16.0f;
            float s = MathHelper.lerp(p, cuboid.minY, cuboid.maxY) / 16.0f;
            float t = MathHelper.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0f;
            matrixStack.translate(r, s, t);
            o = -1.0f * (o * 2.0f - 1.0f);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            this.renderObject(matrixStack, vertexConsumerProvider, i, (Entity)livingEntity, o, p, q, h);
            matrixStack.pop();
        }
    }
}