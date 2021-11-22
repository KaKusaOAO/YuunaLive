package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerBreakBlockGoal;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPlaceBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Locale;

public class YuruEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "Yuru7560_TW";
    public static final String NICKNAME = "優儒";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<YuruEntity> TYPE = getType(ID, YuruEntity::new);

    protected YuruEntity(EntityType<YuruEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/yuru/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(1, new YuunaLivePlayerBreakBlockGoal(this, 1.2, 12, 2, this::isPreferredBlock));
        this.goalSelector.add(1, new YuunaLivePlayerPlaceBlockGoal(this, 1.2, 12, 2,
                Blocks.RED_MUSHROOM.getDefaultState(), this::canPlaceBlock));
    }

    public boolean isPreferredBlock(BlockState state) {
        return state.isOf(Blocks.RED_MUSHROOM);
    }

    public boolean canPlaceBlock(BlockPos pos) {
        boolean lightValid = world.getBaseLightLevel(pos, 0) <= 12;
        boolean canPlace = world.getBlockState(pos).isAir();
        BlockPos groundPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        boolean hasGround = world.getBlockState(groundPos).isOpaqueFullCube(world, pos);
        return lightValid && canPlace && hasGround;
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public String getNickName() {
        return NICKNAME;
    }
}
