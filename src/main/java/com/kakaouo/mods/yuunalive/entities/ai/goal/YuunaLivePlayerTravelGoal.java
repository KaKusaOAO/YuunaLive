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
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
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
            this.updateCountdownTicks = 200;
            BlockPos target = entity.getTravelTarget();
            entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), 1);

            if(entity.getBlockPos().isWithinDistance(entity.getTravelTarget(), 16)) {
                if(entity.getRandom().nextInt(100) == 0) {
                    entity.setWantsToAdventure(false);
                }
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        BlockPos curr = entity.getBlockPos();
        return canStart() && !curr.isWithinDistance(entity.getTravelTarget(), 16);
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
}

