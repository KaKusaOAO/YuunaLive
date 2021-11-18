package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerFindMobGoal extends Goal {
    private YuunaLivePlayerEntity entity;
    private Class<? extends Entity> entityClass;
    private Predicate<? super Entity> predicate;
    private double findRange = 32.0;

    public YuunaLivePlayerFindMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass, Predicate<? super Entity> predicate) {
        this.entity = entity;
        this.entityClass = entityClass;
        this.predicate = predicate;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public YuunaLivePlayerFindMobGoal findRange(double range) {
        findRange = range;
        return this;
    }

    public YuunaLivePlayerFindMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass) {
        this(entity, entityClass, e -> true);
    }

    @Override
    public boolean canStart() {
        return entity.getRandom().nextFloat() < 0.25f * 0.05f &&
                !entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), e -> predicate.test(e) && e.distanceTo(entity) > 8).isEmpty();
    }

    @Override
    public void start() {
        super.start();
        tick();
    }

    @Override
    public void tick() {
        List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
        if (!list.isEmpty()) {
            Entity e = list.get(0);
            entity.getLookControl().lookAt(e, 10.0F, (float)entity.getLookPitchSpeed());
            entity.getNavigation().startMovingTo(e, 1.2000000476837158D);
            if(e.distanceTo(entity) < entity.getRandom().nextInt(5) + 3) {
                stop();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return !entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(8.0), predicate).isEmpty();
    }
}
