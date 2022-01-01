package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
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

@PlayerSkin(value = "textures/entities/yuru/1.png", slim = true)
@PlayerName("Yuru7560_TW")
@PlayerNickname("優儒")
public class YuruEntity extends YuunaLivePlayerEntity {
    protected YuruEntity(EntityType<YuruEntity> entityType, World world) {
        super(entityType, world);
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
}
