package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerFindOwnerGoal extends Goal {
    private YuunaLivePlayerEntity entity;

    public YuunaLivePlayerFindOwnerGoal(YuunaLivePlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return entity.getOwner() == null;
    }

    @Override
    public void tick() {
        var list = entity.world.getEntitiesByClass(YuunaEntity.class, entity.getBoundingBox().expand(entity.getOwnerFindRange()), e -> true);
        if (!list.isEmpty()) {
            entity.setOwner(list.get(0));
        }
    }
}
