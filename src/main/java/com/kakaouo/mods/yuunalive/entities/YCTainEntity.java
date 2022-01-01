package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@PlayerSkin(value = "textures/entities/yc/1.png", slim = true)
@PlayerName("YingChao")
@PlayerNickname("天天")
public class YCTainEntity extends YuunaLivePlayerEntity {
    protected YCTainEntity(EntityType<YCTainEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(1, new YuunaLivePlayerPickupMobGoal(this, YuruEntity.class));
    }

    @Override
    public float getOwnerFindRange() {
        return 128;
    }

    @Override
    public boolean doesChinFacing() {
        return true;
    }
}
