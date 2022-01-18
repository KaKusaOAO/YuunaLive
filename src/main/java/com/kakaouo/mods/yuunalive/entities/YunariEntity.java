package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@PlayerSkin("textures/entities/yunari/1.png")
@PlayerName("yunari930")
@PlayerNickname("洛娜")
@SpawnEggColor(primary = 0x5656e6, secondary = 0x242487)
public class YunariEntity extends YuunaLivePlayerEntity {

    protected YunariEntity(EntityType<YunariEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(1, new YuunaLivePlayerPickupMobGoal(this, KakaEntity.class));
    }
}
