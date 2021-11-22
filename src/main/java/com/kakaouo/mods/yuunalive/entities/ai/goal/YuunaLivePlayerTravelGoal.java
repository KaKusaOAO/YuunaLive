package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.Travellable;
import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class YuunaLivePlayerTravelGoal<T extends MobEntity & Travellable> extends Goal {
    private final T entity;
    private int updateCountdownTicks;

    public YuunaLivePlayerTravelGoal(T entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        return entity.doesWantToAdventure() && entity.getTravelTarget() != null;
    }

    public void start() {
        super.start();
        this.updateCountdownTicks = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 400;
            BlockPos target = entity.getTravelTarget();
            entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), 1);

            BlockPos.Mutable curr = entity.getBlockPos().mutableCopy();
            BlockPos travelTarget = entity.getTravelTarget();
            curr.setY(travelTarget.getY());
            if(curr.isWithinDistance(travelTarget, 16)) {
                if(entity.getRandom().nextInt(100) == 0) {
                    entity.setWantsToAdventure(false);
                }
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        BlockPos.Mutable curr = entity.getBlockPos().mutableCopy();
        BlockPos travelTarget = entity.getTravelTarget();
        curr.setY(travelTarget.getY());
        return canStart() && !curr.isWithinDistance(travelTarget, 16);
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
}

