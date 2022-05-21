package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public class YuunaLivePlayerFindMobGoal extends Goal {
    private final YuunaLivePlayerEntity entity;
    private final Class<? extends Entity> entityClass;
    private final Predicate<? super Entity> predicate;
    private double findRange = 32.0;
    private int updateCountdownTicks;

    public YuunaLivePlayerFindMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass, Predicate<? super Entity> predicate) {
        this.entity = entity;
        this.entityClass = entityClass;
        this.predicate = predicate;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public YuunaLivePlayerFindMobGoal findRange(double range) {
        findRange = range;
        return this;
    }

    public YuunaLivePlayerFindMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass) {
        this(entity, entityClass, e -> true);
    }

    @Override
    public boolean canUse() {
        return entity.getRandom().nextFloat() < 0.25f &&
                !entity.level.getEntitiesOfClass(entityClass, entity.getBoundingBox().inflate(findRange), e -> predicate.test(e) && e.distanceTo(entity) > 8).isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return !entity.level.getEntitiesOfClass(entityClass, entity.getBoundingBox().inflate(8.0), predicate).isEmpty();
    }

    @Override
    public void start() {
        super.start();
        this.updateCountdownTicks = 0;
    }

    @Override
    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            List<? extends Entity> list = entity.level.getEntitiesOfClass(entityClass, entity.getBoundingBox().inflate(findRange), predicate);
            if (!list.isEmpty()) {
                Entity e = list.get(0);
                entity.getLookControl().setLookAt(e, 10.0F, (float) entity.getMaxHeadXRot());
                entity.getNavigation().moveTo(e, 1.2000000476837158D);
                if (e.distanceTo(entity) < entity.getRandom().nextInt(5) + 3) {
                    stop();
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
}
