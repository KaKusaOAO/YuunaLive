package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerPickupMobGoal extends Goal {
    private final YuunaLivePlayerEntity entity;
    private final Class<? extends Entity> entityClass;
    private final Predicate<? super Entity> predicate;
    private double findRange = 32.0;

    public YuunaLivePlayerPickupMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass, Predicate<? super Entity> predicate) {
        this.entity = entity;
        this.entityClass = entityClass;
        this.predicate = e -> {
            return predicate.test(e) && !e.hasVehicle();
        };
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public YuunaLivePlayerPickupMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass) {
        this(entity, entityClass, e -> true);
    }

    public YuunaLivePlayerPickupMobGoal findRange(double range) {
        findRange = range;
        return this;
    }

    @Override
    public boolean canStart() {
        if (entity.hasPassengers()) {
            return false;
        }

        if (entity.getRandom().nextInt(10) != 0) {
            return false;
        } else {
            List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
            return !list.isEmpty();
        }
    }

    @Override
    public void tick() {
        List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
        if (!entity.hasPassengers() && !list.isEmpty()) {
            Entity target = list.get(0);
            if(target == entity) {
                stop();
                return;
            }

            entity.getNavigation().startMovingTo(target, 1.2000000476837158D);
            if(target.distanceTo(entity) < 3) {
                if(target.hasVehicle()) {
                    Entity e = target.getVehicle();
                    entity.tryAttack(e);
                }
                target.startRiding(entity, true);
                if(entity.getTarget() == target) {
                    entity.setTarget(null);
                }
                if(target instanceof YuunaLivePlayerEntity yp) {
                    yp.setPanicking(true);
                }
                entity.playSound(SoundEvents.ENTITY_PIG_SADDLE, 1, 1);
            }
        }
    }

    @Override
    public void start() {
        List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
        if (!list.isEmpty()) {
            entity.getNavigation().startMovingTo(list.get(0), 1.2000000476837158D);
        }
    }
}

