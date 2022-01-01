package com.kakaouo.mods.yuunalive.entities.client.renderer;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

public class YuunaLivePlayerEntityRenderer extends BipedEntityRenderer<YuunaLivePlayerEntity, YuunaLivePlayerEntityModel> {
    public YuunaLivePlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, new YuunaLivePlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new YuunaLivePlayerHeldItemFeatureRenderer<>(this));
        this.addFeature(new YuunaLivePlayerStuckArrowsFeatureRenderer<>(ctx, this));
        // this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new YuunaLivePlayerCapeFeatureRenderer(this));
        // this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this, ctx.getModelLoader()));
        // this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this, ctx.getModelLoader()));
        // this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this, ctx.getModelLoader()));
        // this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this, ctx.getModelLoader()));
        // this.addFeature(new StuckStingersFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
    }

    @Override
    public Identifier getTexture(YuunaLivePlayerEntity entity) {
        return entity.getTexture();
    }

    @Override
    protected boolean hasLabel(YuunaLivePlayerEntity mobEntity) {
        return true;
    }

    private static BipedEntityModel.ArmPose getArmPose(YuunaLivePlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction useAction = itemStack.getUseAction();
            if (useAction == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (useAction == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
        } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    private void setModelPose(YuunaLivePlayerEntity player) {
        YuunaLivePlayerEntityModel playerEntityModel = getModel();
        playerEntityModel.sneaking = player.isInSneakingPose();
        BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
        BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        }
        if (player.getMainArm() == Arm.RIGHT) {
            playerEntityModel.rightArmPose = armPose;
            playerEntityModel.leftArmPose = armPose2;
        } else {
            playerEntityModel.rightArmPose = armPose2;
            playerEntityModel.leftArmPose = armPose;
        }
    }

    @Override
    public void render(YuunaLivePlayerEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(entity);
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
