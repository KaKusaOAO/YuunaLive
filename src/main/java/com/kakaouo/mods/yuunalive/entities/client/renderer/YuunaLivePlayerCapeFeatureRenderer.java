package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Environment(value= EnvType.CLIENT)
public class YuunaLivePlayerCapeFeatureRenderer
        extends RenderLayer<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> {
    public YuunaLivePlayerCapeFeatureRenderer(RenderLayerParent<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, YuunaLivePlayerEntity player, float f, float g, float h, float j, float k, float l) {
        if (!player.canRenderCapeTexture() || player.isInvisible() || player.getCapeTexture() == null) {
            return;
        }

        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.is(Items.ELYTRA)) {
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 0.125);
        double d = Mth.lerp(h, player.prevCapeX, player.capeX) - Mth.lerp(h, player.xo, player.getX());
        double e = Mth.lerp(h, player.prevCapeY, player.capeY) - Mth.lerp(h, player.yo, player.getY());
        double m = Mth.lerp(h, player.prevCapeZ, player.capeZ) - Mth.lerp(h, player.zo, player.getZ());
        float n = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
        double o = Mth.sin(n * ((float)Math.PI / 180));
        double p = -Mth.cos(n * ((float)Math.PI / 180));
        float q = (float)e * 10.0f;
        q = Mth.clamp(q, -6.0f, 32.0f);
        float r = (float)(d * o + m * p) * 100.0f;
        r = Mth.clamp(r, 0.0f, 150.0f);
        float s = (float)(d * p - m * o) * 100.0f;
        s = Mth.clamp(s, -20.0f, 20.0f);
        if (r < 0.0f) {
            r = 0.0f;
        }
        float t = Mth.lerp(h, player.prevStrideDistance, player.strideDistance);
        q += Mth.sin(Mth.lerp(h, player.walkDistO, player.walkDist) * 6.0f) * 32.0f * t;
        if (player.isCrouching()) {
            q += 25.0f;
        }
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(6.0f + r / 2.0f + q));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(s / 2.0f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - s / 2.0f));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entitySolid(player.getCapeTexture()));
        this.getParentModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}