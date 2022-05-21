package com.kakaouo.mods.yuunalive.entities.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;
import java.util.Random;

public class YuunaLivePlayerEntityModel extends HumanoidModel<YuunaLivePlayerEntity> {
    /**
     * The key of the ear model part, whose value is {@value}.
     */
    private static final String EAR = "ear";
    /**
     * The key of the cloak model part, whose value is {@value}.
     */
    private static final String CLOAK = "cloak";
    /**
     * The key of the left sleeve model part, whose value is {@value}.
     */
    private static final String LEFT_SLEEVE = "left_sleeve";
    /**
     * The key of the right sleeve model part, whose value is {@value}.
     */
    private static final String RIGHT_SLEEVE = "right_sleeve";
    /**
     * The key of the left pants model part, whose value is {@value}.
     */
    private static final String LEFT_PANTS = "left_pants";
    /**
     * The key of the right pants model part, whose value is {@value}.
     */
    private static final String RIGHT_PANTS = "right_pants";
    /**
     * All the parts. Used when picking a part to render stuck arrows.
     */
    private final List<ModelPart> parts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart cloak;
    private final ModelPart ear;
    private final boolean thinArms;

    public YuunaLivePlayerEntityModel(ModelPart root, boolean thinArms) {
        super(root, RenderType::entityTranslucent);
        this.thinArms = thinArms;
        this.ear = root.getChild(EAR);
        this.cloak = root.getChild(CLOAK);
        this.leftSleeve = root.getChild(LEFT_SLEEVE);
        this.rightSleeve = root.getChild(RIGHT_SLEEVE);
        this.leftPants = root.getChild(LEFT_PANTS);
        this.rightPants = root.getChild(RIGHT_PANTS);
        this.jacket = root.getChild(PartNames.JACKET);
        this.parts = root.getAllParts().filter(part -> !part.isEmpty()).collect(ImmutableList.toImmutableList());
    }

    public static MeshDefinition getTexturedModelData(CubeDeformation dilation, boolean slim) {
        MeshDefinition modelData = HumanoidModel.createMesh(dilation, 0.0f);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(EAR, CubeListBuilder.create().texOffs(24, 0).addBox(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, dilation), PartPose.ZERO);
        modelPartData.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, dilation, 1.0f, 0.5f), PartPose.offset(0.0f, 0.0f, 0.0f));
        float f = 0.25f;
        if (slim) {
            modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), PartPose.offset(5.0f, 2.5f, 0.0f));
            modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create().texOffs(40, 16).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), PartPose.offset(-5.0f, 2.5f, 0.0f));
            modelPartData.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(5.0f, 2.5f, 0.0f));
            modelPartData.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(-5.0f, 2.5f, 0.0f));
        } else {
            modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), PartPose.offset(5.0f, 2.0f, 0.0f));
            modelPartData.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(5.0f, 2.0f, 0.0f));
            modelPartData.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(-5.0f, 2.0f, 0.0f));
        }
        modelPartData.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create().texOffs(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), PartPose.offset(1.9f, 12.0f, 0.0f));
        modelPartData.addOrReplaceChild(LEFT_PANTS, CubeListBuilder.create().texOffs(0, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(1.9f, 12.0f, 0.0f));
        modelPartData.addOrReplaceChild(RIGHT_PANTS, CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.offset(-1.9f, 12.0f, 0.0f));
        modelPartData.addOrReplaceChild(PartNames.JACKET, CubeListBuilder.create().texOffs(16, 32).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.ZERO);
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
    }

    public void renderEars(PoseStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.ear.copyFrom(this.head);
        this.ear.x = 0.0f;
        this.ear.y = 0.0f;
        this.ear.render(matrices, vertices, light, overlay);
    }

    public void renderCape(PoseStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.cloak.render(matrices, vertices, light, overlay);
    }

    @Override
    public void setupAnim(YuunaLivePlayerEntity livingEntity, float f, float g, float h, float i, float j) {
        super.setupAnim(livingEntity, f, g, h, i, j);
        if(livingEntity.doesChinFacing()) {
            this.head.xRot -= Mth.HALF_PI;
            this.hat.xRot -= Mth.HALF_PI;
        }
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (livingEntity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            if (livingEntity.isCrouching()) {
                this.cloak.z = 1.4f;
                this.cloak.y = 1.85f;
            } else {
                this.cloak.z = 0.0f;
                this.cloak.y = 0.0f;
            }
        } else if (livingEntity.isCrouching()) {
            this.cloak.z = 0.3f;
            this.cloak.y = 0.8f;
        } else {
            this.cloak.z = -1.1f;
            this.cloak.y = -0.85f;
        }

        if(livingEntity.isVehicle()) {
            if(this.leftArmPose == ArmPose.EMPTY) {
                this.leftArm.xRot = Mth.PI;
                this.leftSleeve.xRot = Mth.PI;
            }
            if(this.rightArmPose == ArmPose.EMPTY) {
                this.rightArm.xRot = Mth.PI;
                this.rightSleeve.xRot = Mth.PI;
            }
        }
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
        this.cloak.visible = visible;
        this.ear.visible = visible;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float f = 0.5f * (float)(arm == HumanoidArm.RIGHT ? 1 : -1);
            modelPart.x += f;
            modelPart.translateAndRotate(matrices);
            modelPart.x -= f;
        } else {
            modelPart.translateAndRotate(matrices);
        }
    }

    public ModelPart getRandomPart(Random random) {
        return this.parts.get(random.nextInt(this.parts.size()));
    }
}
