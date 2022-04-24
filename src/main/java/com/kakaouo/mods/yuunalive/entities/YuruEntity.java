package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerBreakBlockGoal;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPlaceBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@PlayerSkin(value = "textures/entities/yuru/2.png", slim = true)
@PlayerName("Yuru7560_TW")
@PlayerNickname("優儒")
@SpawnEggColor(primary = 0xffd7f0, secondary = 0xffa6be)
public class YuruEntity extends YuunaLivePlayerEntity {
    protected YuruEntity(EntityType<YuruEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.addGoal(1, new YuunaLivePlayerBreakBlockGoal(this, 1.2, 12, 2, this::isPreferredBlock));
        this.goalSelector.addGoal(1, new YuunaLivePlayerPlaceBlockGoal(this, 1.2, 12, 2,
                Blocks.RED_MUSHROOM.defaultBlockState(), this::canPlaceBlock));
    }

    public boolean isPreferredBlock(BlockState state) {
        return state.is(Blocks.RED_MUSHROOM);
    }

    public boolean canPlaceBlock(BlockPos pos) {
        boolean lightValid = level.getRawBrightness(pos, 0) <= 12;
        boolean canPlace = level.getBlockState(pos).isAir();
        BlockPos groundPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        boolean hasGround = level.getBlockState(groundPos).isSolidRender(level, pos);
        return lightValid && canPlace && hasGround;
    }
}
