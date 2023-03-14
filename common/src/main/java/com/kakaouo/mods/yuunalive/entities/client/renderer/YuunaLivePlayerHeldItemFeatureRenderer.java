package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class YuunaLivePlayerHeldItemFeatureRenderer<M extends YuunaLivePlayerEntityModel>
        extends ItemInHandLayer<YuunaLivePlayerEntity, M> {
    private static final float HEAD_YAW = -0.5235988f;
    private static final float HEAD_ROLL = 1.5707964f;

    private final ItemInHandRenderer itemInHandRenderer;

    public YuunaLivePlayerHeldItemFeatureRenderer(RenderLayerParent<YuunaLivePlayerEntity, M> context, ItemInHandRenderer renderer) {
        super(context, renderer);
        this.itemInHandRenderer = renderer;
    }

    @Override
    protected void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformationMode, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (stack.is(Items.SPYGLASS) && entity.getUseItem() == stack && entity.swingTime == 0) {
            this.renderSpyglass(entity, stack, arm, matrices, vertexConsumers, light);
        } else {
            super.renderArmWithItem(entity, stack, transformationMode, arm, matrices, vertexConsumers, light);
        }
    }

    private void renderSpyglass(LivingEntity entity, ItemStack stack, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        ModelPart modelPart = ((HeadedModel)this.getParentModel()).getHead();
        float f = modelPart.xRot;
        modelPart.xRot = Mth.clamp(modelPart.xRot, HEAD_YAW, HEAD_ROLL);
        modelPart.translateAndRotate(matrices);
        modelPart.xRot = f;
        CustomHeadLayer.translateToHead(matrices, false);
        boolean bl = arm == HumanoidArm.LEFT;
        matrices.translate((bl ? -2.5f : 2.5f) / 16.0f, -0.0625, 0.0);
        this.itemInHandRenderer.renderItem(entity, stack, ItemTransforms.TransformType.HEAD, false, matrices, vertexConsumers, light);
        matrices.popPose();
    }
}