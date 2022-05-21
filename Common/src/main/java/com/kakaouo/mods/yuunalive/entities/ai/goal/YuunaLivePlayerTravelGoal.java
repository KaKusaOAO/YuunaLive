package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.Travellable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class YuunaLivePlayerTravelGoal<T extends Mob & Travellable> extends Goal {
    private final T entity;
    private int updateCountdownTicks;

    public YuunaLivePlayerTravelGoal(T entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
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
            entity.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1);

            BlockPos.MutableBlockPos curr = entity.blockPosition().mutable();
            BlockPos travelTarget = entity.getTravelTarget();
            curr.setY(travelTarget.getY());
            if(curr.closerThan(travelTarget, 16)) {
                if(entity.getRandom().nextInt(100) == 0) {
                    entity.setWantsToAdventure(false);
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        BlockPos.MutableBlockPos curr = entity.blockPosition().mutable();
        BlockPos travelTarget = entity.getTravelTarget();
        curr.setY(travelTarget.getY());
        return canUse() && !curr.closerThan(travelTarget, 16);
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
}

