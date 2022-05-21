package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

@PlayerSkin(value = "textures/entities/yc/1.png", slim = true)
@PlayerName("YingChao")
@PlayerNickname("天天")
@SpawnEggColor(primary = 0xffffff, secondary = 0xb8fcff)
public class YCTainEntity extends YuunaLivePlayerEntity {
    static boolean shouldBeExcluded() {
        return true;
    }

    protected YCTainEntity(EntityType<YCTainEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.addGoal(1, new YuunaLivePlayerPickupMobGoal(this, YuruEntity.class));
    }

    @Override
    public float getOwnerFindRange() {
        return 128;
    }

    @Override
    public boolean doesChinFacing() {
        return this.getUUID().hashCode() % 2 == 0;
    }
}
