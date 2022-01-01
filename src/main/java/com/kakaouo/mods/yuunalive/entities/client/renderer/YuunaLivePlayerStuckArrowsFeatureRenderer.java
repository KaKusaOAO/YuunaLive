package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class YuunaLivePlayerStuckArrowsFeatureRenderer<M extends YuunaLivePlayerEntityModel>
        extends YuunaLivePlayerStuckObjectsFeatureRenderer<M> {
    private final EntityRenderDispatcher dispatcher;

    public YuunaLivePlayerStuckArrowsFeatureRenderer(EntityRendererFactory.Context context, LivingEntityRenderer<YuunaLivePlayerEntity, M> entityRenderer) {
        super(entityRenderer);
        this.dispatcher = context.getRenderDispatcher();
    }

    @Override
    protected int getObjectCount(YuunaLivePlayerEntity entity) {
        return entity.getStuckArrowCount();
    }

    @Override
    protected void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta) {
        float f = MathHelper.sqrt(directionX * directionX + directionZ * directionZ);
        ArrowEntity arrowEntity = new ArrowEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
        arrowEntity.setYaw((float)(Math.atan2(directionX, directionZ) * 57.2957763671875));
        arrowEntity.setPitch((float)(Math.atan2(directionY, f) * 57.2957763671875));
        arrowEntity.prevYaw = arrowEntity.getYaw();
        arrowEntity.prevPitch = arrowEntity.getPitch();
        this.dispatcher.render(arrowEntity, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);
    }
}