package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class YuunaLivePlayerCancelAttackGoal extends TargetGoal {
    private final YuunaLivePlayerEntity entity;

    public YuunaLivePlayerCancelAttackGoal(YuunaLivePlayerEntity entity) {
        super(entity, false);
        this.entity = entity;
    }

    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        return target != null && entity.getAttackScore(target) < 0;
    }

    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setTarget(null);
        super.start();
    }
}

