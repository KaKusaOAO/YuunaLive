package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class YuunaLivePlayerFindOwnerGoal extends Goal {
    private final YuunaLivePlayerEntity entity;

    public YuunaLivePlayerFindOwnerGoal(YuunaLivePlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getOwner() == null;
    }

    @Override
    public void tick() {
        var list = entity.level.getEntitiesOfClass(YuunaEntity.class, entity.getBoundingBox().inflate(entity.getOwnerFindRange()), e -> true);
        if (!list.isEmpty()) {
            entity.setOwner(list.get(0));
        }
    }
}
