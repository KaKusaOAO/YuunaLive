package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value= EnvType.CLIENT)
public class YuunaLivePlayerCapeFeatureRenderer
        extends FeatureRenderer<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> {
    public YuunaLivePlayerCapeFeatureRenderer(FeatureRendererContext<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, YuunaLivePlayerEntity player, float f, float g, float h, float j, float k, float l) {
        if (!player.canRenderCapeTexture() || player.isInvisible() || player.getCapeTexture() == null) {
            return;
        }
        ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.isOf(Items.ELYTRA)) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 0.125);
        double d = MathHelper.lerp((double)h, player.prevCapeX, player.capeX) - MathHelper.lerp((double)h, player.prevX, player.getX());
        double e = MathHelper.lerp((double)h, player.prevCapeY, player.capeY) - MathHelper.lerp((double)h, player.prevY, player.getY());
        double m = MathHelper.lerp((double)h, player.prevCapeZ, player.capeZ) - MathHelper.lerp((double)h, player.prevZ, player.getZ());
        float n = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        float q = (float)e * 10.0f;
        q = MathHelper.clamp(q, -6.0f, 32.0f);
        float r = (float)(d * o + m * p) * 100.0f;
        r = MathHelper.clamp(r, 0.0f, 150.0f);
        float s = (float)(d * p - m * o) * 100.0f;
        s = MathHelper.clamp(s, -20.0f, 20.0f);
        if (r < 0.0f) {
            r = 0.0f;
        }
        float t = MathHelper.lerp(h, player.prevStrideDistance, player.strideDistance);
        q += MathHelper.sin(MathHelper.lerp(h, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0f) * 32.0f * t;
        if (player.isInSneakingPose()) {
            q += 25.0f;
        }
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(6.0f + r / 2.0f + q));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0f));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - s / 2.0f));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(player.getCapeTexture()));
        this.getContextModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}