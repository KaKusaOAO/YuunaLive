package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerPickupMobGoal extends Goal {
    private YuunaLivePlayerEntity entity;
    private Class<? extends Entity> entityClass;
    private Predicate<? super Entity> predicate;
    private double findRange = 32.0;

    public YuunaLivePlayerPickupMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass, Predicate<? super Entity> predicate) {
        this.entity = entity;
        this.entityClass = entityClass;
        this.predicate = predicate;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public YuunaLivePlayerPickupMobGoal(YuunaLivePlayerEntity entity, Class<? extends Entity> entityClass) {
        this(entity, entityClass, e -> true);
    }

    public YuunaLivePlayerPickupMobGoal findRange(double range) {
        findRange = range;
        return this;
    }

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

    public void tick() {
        List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
        List<Entity> passengers = entity.getPassengerList();
        if (passengers.isEmpty() && !list.isEmpty()) {
            Entity target = list.get(0);
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

    public void start() {
        List<? extends Entity> list = entity.world.getEntitiesByClass(entityClass, entity.getBoundingBox().expand(findRange), predicate);
        if (!list.isEmpty()) {
            entity.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158D);
        }
    }
}

