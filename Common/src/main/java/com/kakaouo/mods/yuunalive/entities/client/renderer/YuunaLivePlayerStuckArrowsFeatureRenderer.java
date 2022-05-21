package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;

public class YuunaLivePlayerStuckArrowsFeatureRenderer<M extends YuunaLivePlayerEntityModel>
        extends YuunaLivePlayerStuckObjectsFeatureRenderer<M> {
    private final EntityRenderDispatcher dispatcher;

    public YuunaLivePlayerStuckArrowsFeatureRenderer(EntityRendererProvider.Context context, LivingEntityRenderer<YuunaLivePlayerEntity, M> entityRenderer) {
        super(entityRenderer);
        this.dispatcher = context.getEntityRenderDispatcher();
    }

    @Override
    protected int getObjectCount(YuunaLivePlayerEntity entity) {
        return entity.getArrowCount();
    }

    @Override
    protected void renderObject(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta) {
        float f = Mth.sqrt(directionX * directionX + directionZ * directionZ);
        Arrow arrowEntity = new Arrow(entity.level, entity.getX(), entity.getY(), entity.getZ());
        arrowEntity.setYRot((float)(Math.atan2(directionX, directionZ) * 57.2957763671875));
        arrowEntity.setXRot((float)(Math.atan2(directionY, f) * 57.2957763671875));
        arrowEntity.yRotO = arrowEntity.getYRot();
        arrowEntity.xRotO = arrowEntity.getXRot();
        this.dispatcher.render(arrowEntity, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);
    }
}