package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class YuunaLivePlayerBowAttackGoal extends Goal {
    private final YuunaLivePlayerEntity actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;

    public YuunaLivePlayerBowAttackGoal(YuunaLivePlayerEntity actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canUse() {
        if (this.actor.getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.actor.isHolding(Items.BOW);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.actor.getNavigation().isDone()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.actor.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAggressive(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.actor.stopUsingItem();
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = this.actor.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl = this.actor.getSensing().hasLineOfSight(livingEntity);
        boolean bl3 = bl2 = this.targetSeeingTicker > 0;
        if (bl != bl2) {
            this.targetSeeingTicker = 0;
        }
        this.targetSeeingTicker = bl ? ++this.targetSeeingTicker : --this.targetSeeingTicker;
        if (d > (double)this.squaredRange || this.targetSeeingTicker < 20) {
            this.actor.getNavigation().moveTo(livingEntity, this.speed);
            this.combatTicks = -1;
        } else {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        }
        if (this.combatTicks >= 20) {
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                boolean bl4 = this.movingToLeft = !this.movingToLeft;
            }
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }
        if (this.combatTicks > -1) {
            if (d > (double)(this.squaredRange * 0.75f)) {
                this.backward = false;
            } else if (d < (double)(this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            this.actor.getMoveControl().strafe(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
            this.actor.lookAt(livingEntity, 30.0f, 30.0f);
        } else {
            this.actor.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        }
        if (this.actor.isUsingItem()) {
            int i;
            if (!bl && this.targetSeeingTicker < -60) {
                this.actor.stopUsingItem();
            } else if (bl && (i = this.actor.getTicksUsingItem()) >= 20) {
                this.actor.stopUsingItem();
                ((RangedAttackMob)this.actor).performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                this.cooldown = this.attackInterval;
            }
        } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            this.actor.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.actor, Items.BOW));
        }
    }
}

