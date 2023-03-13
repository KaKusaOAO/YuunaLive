package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;

public class YuunaLivePlayerEntityRenderer extends HumanoidMobRenderer<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> {
    public YuunaLivePlayerEntityRenderer(EntityRendererProvider.Context ctx, boolean slim) {
        super(ctx, new YuunaLivePlayerEntityModel(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))
        ));
        this.addLayer(new YuunaLivePlayerHeldItemFeatureRenderer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new YuunaLivePlayerStuckArrowsFeatureRenderer<>(ctx, this));
        this.addLayer(new YuunaLivePlayerCapeFeatureRenderer(this));

        // this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
        // this.addLayer(new ElytraLayer(this, context.getModelSet()));
        // this.addLayer(new ParrotOnShoulderLayer(this, context.getModelSet()));
        // this.addLayer(new SpinAttackEffectLayer(this, context.getModelSet()));
        // this.addLayer(new BeeStingerLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(YuunaLivePlayerEntity entity) {
        return entity.getTexture();
    }

    @Override
    protected boolean shouldShowName(YuunaLivePlayerEntity mobEntity) {
        return true;
    }

    private static HumanoidModel.ArmPose getArmPose(YuunaLivePlayerEntity player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
            UseAnim useAction = itemStack.getUseAnimation();
            if (useAction == UseAnim.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if (useAction == UseAnim.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAnim.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if (useAction == UseAnim.CROSSBOW && hand == player.getUsedItemHand()) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAnim.SPYGLASS) {
                return HumanoidModel.ArmPose.SPYGLASS;
            }
        } else if (!player.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    private void setModelPose(YuunaLivePlayerEntity player) {
        YuunaLivePlayerEntityModel playerEntityModel = getModel();
        playerEntityModel.crouching = player.isCrouching();
        HumanoidModel.ArmPose armPose = getArmPose(player, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose armPose2 = getArmPose(player, InteractionHand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            armPose2 = player.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }
        if (player.getMainArm() == HumanoidArm.RIGHT) {
            playerEntityModel.rightArmPose = armPose;
            playerEntityModel.leftArmPose = armPose2;
        } else {
            playerEntityModel.rightArmPose = armPose2;
            playerEntityModel.leftArmPose = armPose;
        }
    }

    @Override
    public void render(YuunaLivePlayerEntity entity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        this.setModelPose(entity);
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
